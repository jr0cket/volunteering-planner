(ns volunteering-planner.handler
  (:require [compojure.core :refer :all]
            [compojure.route :as route]
            [ring.middleware.defaults :refer [wrap-defaults site-defaults]])
  (:require [hiccup.core :as html]
            [hiccup.form :as form]
            [ring.util.anti-forgery :as af]
            [ring.util.response :as redirect]
            [clojure.string :as string]))

;;; Task slots - build a shed, etc.
(def tasks ["Build a shed" "Paint Shed" "Weeding" "Build Fence" "Mix Cement" "Tree Surgery"] )

;;; Times slots eg 1pm-2pm welcome desk
(def time-slots [:one :two :three :four])

(def volunteers ["Tom" "Dick" "Harriet" "Jill" "Jack"])

(defn task-assignement
  "Construct a map of volunteers assigned to tasks [stateful using atom]"
  [tasks]
  (into {} (map #(vector % [])
                tasks)))

(def map-assign (atom (task-assignement tasks)))

(defn tasks-available
  "A simple Ring handler for the /tasks-available route. This returns a map containing my html, which Ring translates back into an HTTP response"
  [request]
  (html/html [:ul
              (for [t tasks]
                [:li t])]))

(defn view-missions
  "Display the tasks available as a table and allow volunteers to be added to one or more tasks"
  []
  (html/html
   [:table
    [:th
     [:td "Task"]
     [:td "Assigned"]]
    (for [t tasks]
      [:tr
       [:td t]
       [:td (string/join ", " (get @map-assign t))]])]
   (form/form-to [:post "/add-assignment"]
                 (form/text-field {:placeholder "Enter task"} :task)
                 (form/text-field {:placeholder "Enter volunteer"} :volunteer)
                 (af/anti-forgery-field)
                 (form/submit-button "Submit"))))

(defn handle-assignement [assignments task volunteer]
  (if (some #(= task %) tasks)
    (update-in assignments [task] #(conj % volunteer))))

(defroutes app-routes
  (GET "/" [] "Hello World")
  (GET "/tasks-available" [] tasks-available)
  (GET "/mission" [] (view-missions))
  (POST "/add-assignment" [task volunteer] 
        (do (swap! map-assign handle-assignement task volunteer)
            (redirect/redirect-after-post "/mission")))
  (route/not-found "Not Found"))

(def app
  (wrap-defaults app-routes site-defaults))




