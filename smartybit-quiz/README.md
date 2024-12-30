# Smartybit Quiz

The quiz engine will work as follows: A player needs to create questionnaires with various questions. This data can be in Redis. I need profile data which can be stored in mongodb. There is an authorization system that is required that grants access to different questionnaires based on the player's preference: public or private. There needs to be a quiz engine that can start a quiz, get the next question depending on the correctness of the previous question making it either harder or easier depending on the correctness of the streak of answers. The scoreboard can be a relational database like postgres (unless you suggest otherwise). It needs to tally the score of each player as follows: total score is the accumulated score from the first day of playing, the run is the total score during a streak (playing consistently each day), latest score is to tally the score for the last 7 days.

To implement your quiz engine in **Polylith** using **Clojure**, we'll break the system down into modules that fit within Polylith's component, base, and interface architecture. Here’s an outline of how to structure the project and the components needed.

---

## Core Design and Data Flow

- **Data in Redis**: Stores questionnaire and question-related data. Redis is fast and works well for the kind of transient/question flow logic required.
- **MongoDB for Profiles**: MongoDB stores player profiles with their preferences and access settings (public/private).
- **Postgres for Scoreboard**: A relational database like Postgres handles detailed scoreboard logic (aggregations, streaks, etc.).
- **Authorization**: Based on preferences (public/private), a layer validates access to quizzes.
- **Quiz Logic**: Implements adaptive quiz mechanics (harder/easier questions based on streaks).

## Polylith Architecture

Polylith applications separate concerns into **components**, **bases**, and **interfaces**:

### Bases

- `api`: Handles HTTP endpoints for the frontend or other consumers.
- `worker`: Handles quiz-related processing tasks, e.g., starting quizzes and evaluating responses.

### Components

- `auth`: Manages authorization logic.
- `profile`: Fetches and updates user profile data in MongoDB.
- `questionnaire`: Manages Redis-based questionnaire data.
- `quiz-engine`: Implements adaptive quiz mechanics.
- `scoreboard`: Interfaces with Postgres to calculate total scores, streaks, and weekly tallies.
- `streak`: Calculates streak-related scores.

### Component Breakdown

Here’s a detailed look at the core components:

#### `auth` Component

- **Responsibility**: Validates whether a player can access a quiz.
- **Functions**:
  - `validate-access`: Check if the quiz is public or matches the player’s preferences.

#### `profile` Component

- **Responsibility**: Manages player profiles.
- **Database**: MongoDB.
- **Functions**:
  - `get-profile`: Fetch profile data.
  - `update-profile`: Update preferences or other data.

#### `questionnaire` Component

- **Responsibility**: Stores and retrieves questionnaires and questions.
- **Database**: Redis.
- **Functions**:
  - `get-questions`: Retrieve questions for a given quiz.
  - `store-questionnaire`: Save questionnaires.
  - `update-questionnaire`: Add or modify questions.

#### `quiz-engine` Component

- **Responsibility**: Adaptive question flow and quiz session handling.
- **Functions**:
  - `start-quiz`: Initializes a quiz session.
  - `get-next-question`: Returns the next question based on the streak/correctness of previous answers.
  - `evaluate-answer`: Determines correctness and difficulty adjustment.

#### `scoreboard` Component

- **Responsibility**: Handles score tallying and reporting.
- **Database**: Postgres.
- **Functions**:
  - `tally-score`: Update the total, streak, and weekly scores for a player.
  - `get-scoreboard`: Fetch leaderboard data.

#### `streak` Component

- **Responsibility**: Calculates streak-related data.
- **Functions**:
  - `calculate-streak`: Determine the current streak of consistent play.

## Workflow Example

1. **Starting a Quiz**

   - API requests the `quiz-engine/start-quiz` function.
   - The `auth` component ensures the player can access the quiz.
   - The `questionnaire` component fetches questions from Redis.

2. **Answering Questions**

   - `quiz-engine/evaluate-answer` determines if the answer is correct.
   - Difficulty of the next question adjusts based on streaks using `quiz-engine/get-next-question`.

3. **Updating Scores**
   - The `scoreboard/tally-score` function updates:
     - Total score.
     - Streak score (via `streak/calculate-streak`).
     - Weekly score.

## Example Polylith Configuration

In `deps.edn`:

```clojure
{:aliases {:dev {:extra-paths ["components" "bases" "interfaces"]}}
 :paths ["modules" "projects"]
 :deps {org.clojure/clojure {:mvn/version "1.11.1"}
        com.taoensso/carmine {:mvn/version "3.1.0"} ;; Redis
        org.mongodb/mongodb-driver-sync {:mvn/version "4.11.0"}
        org.postgresql/postgresql {:mvn/version "42.6.0"}}}
```

---

## Polylith Component Example

### Quiz engine

```clojure:title=components/quiz-engine/src/quiz_engine/core.clj
(ns quiz-engine.core)

(defn start-quiz [player-id quiz-id]
  ;; Validate access and initialize session
  (let [authorized? (auth/validate-access player-id quiz-id)]
    (if authorized?
      {:status :ok
       :questions (questionnaire/get-questions quiz-id)}
      {:status :forbidden})))

(defn get-next-question [session-id answer]
  ;; Evaluate answer and adjust difficulty
  (let [{:keys [correct? streak]} (evaluate-answer session-id answer)]
    (if correct?
      (questionnaire/get-question-for-difficulty session-id :harder)
      (questionnaire/get-question-for-difficulty session-id :easier))))

(defn evaluate-answer [session-id answer]
  ;; Evaluate the answer correctness and streak logic
  (let [correct? (questionnaire/validate-answer session-id answer)]
    {:correct? correct?
     :streak (if correct?
               (streak/increment session-id)
               (streak/reset session-id))}))
```

## Advantages of this Architecture

- **Scalability**: Each component can evolve independently, making it easier to scale functionality.
- **Extensibility**: Adding new features, such as time-limited quizzes, is straightforward.
- **Performance**: Redis provides high-speed access to questionnaire data, while Postgres is reliable for relational scoreboard queries.
