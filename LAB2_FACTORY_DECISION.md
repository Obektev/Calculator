# Lab 2: Factory Method decision

Factory Method was selected because calculator buttons belong to one product family but have different behavior.

Benefits in this project:
- Encapsulates button creation logic in one place.
- Makes UI code independent from concrete button classes.
- Simplifies extension with new button types such as memory or scientific functions.
- Keeps existing behavior unchanged when adding new tokens.

This approach fits current requirements and keeps code open for extension with minimal modification.
