# Instructions

These classes have similar-ish objectives and that is why they are grouped together

Expressions
---
Expressions are the responsibles for creating the "Continuation" that the machine must then execute.


Continuations
---

These are the stoppable execution steps that the machine has to perform when evaluating the LISP code. <br>
The Continuation name is a poor design choice because as Jan-Willem van de Meent's et al. ***"[An Introduction to Probabilistic Programming](https://arxiv.org/pdf/1809.10756#page=166&zoom=100,148,840)"*** puts it: _"We can think of the continuation as a “snapshot” of an intermediate state in the computation, in the sense that it represents both the expressions that have been evaluated so far, and the expressions that need to be evaluated to complete the computation."_ This, however, is not our case as the continuations do not hold the following steps nor the environment as a whole, but rather the minimum required information to perform their individual operations, regardless of that they are the representation of the most atomic of operations.

