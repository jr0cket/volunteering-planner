(ns volunteering-planner.handler
  (:require [compojure.core :refer :all]
            [compojure.route :as route]
            [ring.middleware.defaults :refer [wrap-defaults site-defaults]])
  (:require [hiccup.core :as html]
            [hiccup.form :as form]
            [ring.util.anti-forgery :as af]
            [ring.util.response :as redirect]
            [clojure.string :as string]))


;;; Task slots - build a shed

(def tasks ["Build a shed" "Paint Shed" "Weeding" "Build Fence" "Mix Cement" "Tree Surgery"] )

;;; Times slots eg 1pm-2pm welcome desk
(def time-slots [:one :two :three :four])

(def volunteers ["Tom" "Dick" "Harriet" "Jill" "Jack"])

(defn task-assignement [tasks]
  (into {} (map #(vector % [])
                tasks)))
(def map-assign (atom (task-assignement tasks)))

(defn tasks-available [request] 
  (html/html [:ul 
              (for [t tasks]
                [:li t])]))

(defn view-missions []
  (html/html [:table [:th [:td "Task"] [:td "Assigned"]] 
              (for [t tasks]
                [:tr [:td t] [:td
                              (string/join ", " (get @map-assign t))]])]
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




