### My solutions to the [Advent of Code 2024](https://adventofcode.com/2024/) coding challenge

I know, this is a pile of cow dung. It was hard to write, so it should be hard to read. 😎 Don't be too judgmental, it
works.

My solutions from previous years can be found [here](https://github.com/bercee/AoC2023)
and [here](https://github.com/bercee/AoC2022).

#### Automatic download and submission

I am under the illusion that if I automatize the pulling of inputs and submitting of results, I will be faster and
therefore achieve a better place on Leaderboards. Even though this assumption was proved wrong on many occasions, I
still
enjoyed preparing the scripts.

- Place your session ID in a file `session_id.txt`
- Edit `build.gradle` to configure what day you are solving.
- You should create a subclass of `Solver` in `solvers.year2024.Day5`, or something like that, and it will be
  dynamically instantiated and executed by `Main`
- You can run `Main.main` (or `./gradlew runMainClass --args="testPart1"`) with one of the following arguments:
    - `"testPart1"`
    - `"testPart2"`
    - `"solvePart1"`
    - `"solvePart2"`
    - `"solveAndSubmitPart1"`
    - `"solveAndSubmitPart2"`
- Stuff will be place in `assets/`
    - As per the requirement of [Advent of Code](https://adventofcode.com/2024/about), personal inputs are pushed to my
      repo. You can have your own.
- I am parsing the task description each day, and try to extract the test input data and the results for both
  parts. [60% of the time, it works every time](https://www.youtube.com/watch?v=pjvQFtlNQ-M). So - again - don't be too
  judgmental (but if you have a better way of doing it, let me know.)
    - Sometimes it fails, and asks you to type the test result on the console. 