# Lab 3: MVVM architecture notes

The calculator is split into three layers:

- Model: CalculatorModel and CalculatorEngine process business logic and button execution.
- ViewModel: CalculatorViewModel exposes immutable UI state and handles user intents.
- View: Compose App reads state and forwards UI actions and keyboard input as tokens.

Why this improves the project:
- UI does not contain arithmetic rules.
- Business logic can be tested without rendering UI.
- Future UI changes do not require rewriting core computation logic.
