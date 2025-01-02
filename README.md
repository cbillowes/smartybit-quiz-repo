# Smartybit

The purpose of this repository is to drive learning, curiosity and hands-on experimentation with DDD, Clean Architecture, Polylith, Clojure & Next.js.

## Domain

The **core domain** of Smartybit Quiz is the **Quiz Engine**, which delivers the app's unique value through adaptive difficulty, streak tracking, and real-time decision-making to enhance player engagement and satisfaction. It acts as the central hub, seamlessly integrating with supporting contexts like Questionnaire Management, Scoreboard, and Player Profile to deliver a personalized and dynamic quiz experience.

### **Ubiquitous Language**

The terms used consistently across the application to ensure clarity:

1. **Player**: A user participating in quizzes.
1. **Questionnaire**: A collection of questions curated by a player.
1. **Question**: An individual item in a questionnaire, which can vary in type (e.g., multiple-choice, true/false).
1. **Quiz**: A sequence of questions delivered to the player based on a selected questionnaire.
1. **Access Level**: The visibility of a questionnaire (public or private).
1. **Streak**: A consecutive number of days a player has engaged with the app.
1. **Difficulty Level**: The relative complexity of a question, which adapts based on player performance.
1. **Scoreboard**: A representation of a player's performance metrics:
   - **Total Score**: Overall points since starting the app.
   - **Run Score**: Points accumulated during a streak.
   - **Latest Score**: Points accrued in the past 7 days.

### Rules

#### Player Profile Rules

Each player must register with a unique email address and can update their profile preferences, such as notification settings and default quiz configurations. Players earn streaks by engaging with quizzes daily, and these streaks reset if they miss a day. Player preferences are used to tailor the quiz experience and access permissions.

#### Questionnaire Management Rules

Players can create, edit, and delete their own questionnaires, which must contain at least one question to be valid. Questions can vary in type (e.g., multiple-choice, true/false), and questionnaires can be marked as either public (available to everyone) or private (restricted access). Public questionnaires cannot be deleted if they are actively in use by other players.

#### Authorization Rules

Only the creator of a questionnaire can set it as public or private. Private questionnaires require explicit access permissions, while public ones are open to all players. Unauthorized access attempts are logged, and public questionnaires may include optional restrictions based on player rank or other criteria.

#### Quiz Engine Rules

The quiz engine adapts dynamically to player performance, adjusting the difficulty of questions based on correct or incorrect answers. Streaks of correct answers further influence the quiz progression. Quizzes can be timed, with progress auto-saved when time expires. Partially completed quizzes do not count toward scores, and players can quit a session at any time.

#### Scoreboard Rules

Player performance is tracked using three key metrics: **Total Score** (cumulative points since joining), **Run Score** (points earned during a streak of consecutive daily play), and **Latest Score** (points from the past 7 days). Scores are updated after each quiz session, and leaderboards for public quizzes are refreshed in real time.

#### General Rules

Players cannot attempt their own private questionnaires unless explicitly allowed, which is useful for testing. Public questionnaires can be reported for inappropriate content, triggering admin review. Repeated violations may result in disabling the offending questionnaire. These rules ensure a fair, engaging, and secure experience for all players.

## Getting Started

I created the project as follows:

1. Install [Polylith CLI](https://cljdoc.org/d/polylith/clj-poly/0.2.21/doc/install).
1. Add the `repl` and `outdated` aliases to your `deps.edn` file:

   ```clojure
   {:aliases
    {...
     :repl {:exec-fn clojure.core.server/start-server
            :exec-args {:name "repl-server"
                        :port 5555
                        :accept clojure.core.server/repl
                        :server-daemon false}}

     :outdated {:main-opts ["-m" "antq.core"]
                :extra-deps {com.github.liquidz/antq {:mvn/version "2.11.1260"}}}}}
   ```

1. You can now start a REPL server, run the Polylith CLI and check if dependencies are outdated using the following commands:

   | Command                 | Description                        |
   | ----------------------- | ---------------------------------- |
   | `clj -M:dev`            | Create a development environment   |
   | `clj -M:poly test :dev` | Run the tests                      |
   | `clj -M:repl`           | Start a REPL server                |
   | `clj -M:poly`           | Run the Polylith CLI               |
   | `clj -M:outdated`       | Check if dependencies are outdated |

1. Run `clj -M:outdated` to check for outdated dependencies.
1. Update `deps.edn` with the latest dependencies, if necessary.

## Creating Components

1. The **Quiz Engine** is the heart of Smartybit, so we will start here. We are going to interact with the data using the REPL first. We won't start with outside constructs and we'll mock as much as we need to to get started.

   ```bash
   cd smartybit # if you are not already in the directory
   clj -M:poly create component name:quiz-engine
   # Remember to add :local/root dependencies to dev and project 'deps.edn' files.
   ```

1. Open `deps.edn` and:
   - add `"components/quiz-engine/src"` to `:extra-paths` in `:dev`.
   - add `"components/quiz-engine/test"` to `:extra-paths` in `:test`.
