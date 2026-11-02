# Generic migration ECA execution (WP1)

## Catalog and coverage

`migration/tools/eca_catalog.py` reads the ECA resources declared in the ten
ported application component descriptors. The Gradle `generateEcaCatalog` task
generates one JSON resource in the common module's build directory and
`docs/migration/eca-coverage.md`. Neither generated output should be edited.
Original XML remains unchanged. Rule IDs are component/resource/one-based
ordinal, excluding XML comments; inserting a preceding rule changes later IDs.
The generator rejects exclusion IDs that no longer exist.

Coverage is a static migration-source candidate inventory, not a statement that
every rule has been exercised or that every candidate is registered in every
standalone application. A string reference is evidence of a candidate, not proof
of implementation. Runtime service registrations are authoritative: unavailable
targets are skipped with a debug log and a counter. Cross-component targets run
only if both components are registered in the same application; there is no
remote-service fallback. Mixed-target rules execute available actions and count
the others. Unavailable condition services disable the entire rule.

## Dispatch and transaction boundaries

`MigrationServiceDispatcher` runs the engine around registered handlers and
entity-auto services, after the existing authorization checks. The engine copies
the request context, merges service results before output rules, and preserves
error/failure response messages instead of replacing them with success.

Service events follow the original dispatcher phases: global rollback/commit
registration, auth, in-validate, invoke, handler, out-validate, commit, global
commit post-run registration, return. The same Spring transaction covers local
events; commit rules are **before**, not after, the database commit. Global
actions use transaction synchronization and independent transactions after the
appropriate completion outcome. Async actions are deferred until commit but run
serially on the completion thread; they are not durable jobs. `persist` and
`pool-name` do not create an OFBiz JobSandbox or scheduler queue.

OFBiz's return evaluation occurs after its own transaction-finalization block.
The migration deliberately retains return rules inside the encompassing Spring
transaction, including when called within a larger transaction. There is no
independent OFBiz parameter/output-validation pipeline or service semaphore.
Exceptions abort the current invocation; ordinary error/failure result maps
honor `run-on-error` and `run-on-failure` on subsequent phases. Error results mark
the transaction rollback-only; failure results do not automatically do so.
An ignored action exception may still make a participating Spring transaction
rollback-only if its underlying transactional service or persistence provider
does so. `ignore-error` cannot repair a database-aborted transaction.

Deferred actions capture their context and authentication when scheduled. Their
results cannot change an already-returned response. Completion failures are
logged and counted, not thrown back as a fictitious rollback of committed data.
Without synchronization, commit actions run immediately and rollback actions
have no completion to observe. Explicit action `new-transaction` uses
`REQUIRES_NEW`. Action input/output filtering comes from original service
attributes, entity-auto attributes, and inherited service interfaces.

## Internal calls

`EcaServiceInterceptor` wraps Spring migration beans' Map-to-Map service methods.
An exact registered service/method-name match runs through the engine even when
another migration bean calls it directly. `@EcaService` maps methods whose names
have changed; the internal accounting transaction-and-entries path uses it.
Already-dispatched methods are not wrapped twice. Registered method references
still run through the dispatcher's outer ECA envelope.

As with ordinary Spring AOP, self-invocation bypasses the proxy. Private methods,
final classes, non-Map signatures, and differently named methods without
`@EcaService` are not automatically service boundaries. Existing business-flow
helpers in those categories remain direct code; additional ports should expose
an intercepted boundary or explicitly dispatch the service. This is not a
bytecode-level replacement for every original `dispatcher.runSync` call.

## Conditions, actions, identity, and recursion

Value and field conditions support the inventoried equality, inequality,
emptiness and ordered comparisons, explicit Boolean/number/Timestamp conversion,
and dotted Map lookups. Boolean and/or/xor/not nodes are supported even though
the current source inventory uses flat AND conditions. Condition services receive
`serviceContext`, `serviceName`, and `userLogin`; `conditionReply=true` is required.
Sets support literal/environment lookup, `${...}` expansion, nested fields,
upper/lower case, append, numeric and hash-code formats. Java/entity-name formats
and arbitrary OFBiz FlexibleMapAccessor object/property expressions are not
implemented; entity action `value-attr` receives the migrated entity instance,
not a GenericValue.

Actions honor sync/async, result-to-context, result-to-result, result-map-name,
ignore-error/failure, and run-as-user. XML schema defaults apply: result-to-context
and ignore-error/failure default true; result-to-result and new-transaction
default false. System actions use a scoped system authentication with the local
component admin authority, restored in a finally block. Other named users must
exist in the configured Spring UserDetailsService. A userLogin field is context,
not authentication, and does not grant permissions.

Each phase deduplicates successful actions by target service, as OFBiz does.
Rule ancestry prevents immediate and deferred cycles; nesting is limited to 32.
The engine exposes per-rule counters for matched/executed, unavailable targets,
unsupported conditions, disabled rules, action errors, and recursion suppression.

## Entity events and hand-wired effects

Entity-auto create/update/expire/delete paths invoke return EECAs with the
persisted entity fields. Update and expire correspond to `store`, delete to
`remove`; operation lists such as `create-store` and `any` are supported. Results
can update non-key fields. Direct repository/JPA writes in component-specific
handlers are **not** globally intercepted: no Hibernate-wide listener is
installed, and there is no emulation of find/cache events, old GenericValues,
or arbitrary delegator operations. These remain outside entity-auto coverage.

`eca-disabled-rules.json` is the explicit, reviewable rule-ID/reason exclusion
list for effects already implemented inline or through existing commit hooks.
It is an input to generation, not a generated catalog to edit. When an inline
effect is removed in favor of generic execution, remove its exclusion too.

## Test expectation changes

* Party `MultiRollbackTest.ac32_duplicateRoleReturnsConflictAndRollsBackAllRows`:
  `createPartyGroup` now creates its original ECA default `_NA_` role. The batch
  duplicates `CUSTOMER` instead, retaining a successful first insert followed by
  a conflict and rollback. The final assertion expects only the preexisting
  `_NA_` role rather than an empty role list. The old assertion encoded the
  missing-ECA behavior.
