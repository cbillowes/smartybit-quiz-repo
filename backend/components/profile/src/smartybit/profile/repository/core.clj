(ns smartybit.profile.repository.core)

(defprotocol ProfileRepository
  (get-profiles [this])

  (get-profile [this id])

  (create-profile [this profile])

  (update-profile [this id profile])

  (delete-profile [this id]))