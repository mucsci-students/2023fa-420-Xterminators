# 2023fa-420-Xterminators

## 📝 Description
This project is an implementation of the Spelling Bee game in Java 20, created
as a requirement of the CSCI 420 course. It allows players to play this word
puzzle through a graphical user interface (GUI) or a command-line interface (CLI).

## 📋 Prerequisites
The following dependencies are needed to build and run this application:

- Java 20 is installed on your system. You can download it [here](https://www.oracle.com/java/technologies/downloads/).

## 🛠️ Build and Run Instructions
To build and run the application, use the following steps:

1. Clone the repository to your local machine:
```
git clone https://github.com/mucsci-students/2023fa-420-Xterminators.git
```

2. Navigate to the project directory:
```
cd 2023fa-420-Xterminators
```

3. Run the application with the GUI (default):
```
./gradlew run
```
or run the application with the CLI:
```
./gradlew --console plain run --args="--cli"
```

## 👥 Team Members

- JJ Snader
- Jonathan Hart
- Luke Vance

## 🎨 Design Patterns Used

- MVC (Model-View-Controller)
    - MVC was used as the primary design pattern for the application.
    - The `Puzzle` class is used as the model.
    - The `CLIController` and `GuiController` classes are used as controllers
      for communication between the view and model in their respective UI modes.
    - The `CLIView` and `GuiView` were used as frontend views between the user
      and the application for their respective UI modes.
- Builder Pattern
    - The builder pattern was used for most creations of the `Puzzle` object.
    - A `PuzzleBuilder` would be constructed and then given information to build
      a `Puzzle` with either a known root word and primary letter, a random
      primary letter, or a fully random puzzle.
- Singleton Pattern
    - The singleton pattern was used for the `Puzzle` as only one puzzle should
      ever be in progress at any one time.
    - The `Puzzle.getInstance` function was used in the controllers to allow
      them not to store a puzzle themselves.
- Abstract Factory Pattern
    - The abstract factory pattern was used in `App` to create and run the UI.
      If a CLI was requested, a `CLIFactory` was used, and if a GUI was
      requested, a `GuiFactory` was used.
