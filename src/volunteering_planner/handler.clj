(ns volunteering-planner.handler
  (:require [compojure.core :refer :all]
            [compojure.route :as route]
            [ring.middleware.defaults :refer [wrap-defaults site-defaults]])
  (:require [hiccup.core :as html]))


;;; Task slots - build a shed

(def tasks ["Build a shed" "Paint Shed" "Weeding" "Build Fence" "Mix Cement" "Tree Surgery"] )

;;; Times slots eg 1pm-2pm welcome desk
(def time-slots [:one :two :three :four])


(defn tasks-available [request] 
  (html/html [:ul 
              (for [t tasks]
                [:li t])]))

(defroutes app-routes
  (GET "/" [] "Hello World")
  (GET "/tasks-available" [] tasks-available)
  (route/not-found "Not Found"))

(def app
  (wrap-defaults app-routes site-defaults))




