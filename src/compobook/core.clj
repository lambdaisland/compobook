(ns compobook.core
  (:require [compojure.core :refer [defroutes GET]]
            [hiccup.core :refer [html]]
            [ring.adapter.jetty :refer [run-jetty]]
            [ring.middleware.reload :refer [wrap-reload]]
            [ring.util.response :refer [redirect]])
  (:import java.util.UUID))

(def initial-contacts [{:id #uuid "a9481ce1-8711-4f4f-af5c-6fb703f5b632",
                        :name "Master Sammy",
                        :email "mastersammy@lambdaisland.com",
                        :twitter "@callmemasta"}
                       {:id #uuid "749db389-7702-4a6b-a761-ee5141451def",
                        :name "Brock Samson",
                        :email "brocksamson@yahoo.com",
                        :twitter "@bigboysamson"}
                       {:id #uuid "e79f8935-3eb7-4774-aa6e-39d862905fc8",
                        :name "Lana Kane",
                        :email "lanakane@lambdaisland.com",
                        :twitter "@gatorkane"}])

(def db (atom {:contacts initial-contacts}))

(defn query-contacts []
  (:contacts @db))

(defn contact-list-view [contacts]
  [:ul
   (for [{:keys [name id]} contacts]
     [:li
      [:a {:href (str "/contacts/" id)} name]])])

(defn find-contact [id]
  (->> @db
       :contacts
       (filter #(= id (:id %)))
       first))

(defn contact-view [{:keys [name email twitter] :as contact}]
  [:div
   [:ul.contact
    [:li "Name:" name]
    [:li "Email:" email]
    [:li "Twitter:" twitter]]
   [:a {:href "/contacts"} "← Back"]])

(defroutes app-routes
  (GET "/" _
    (redirect "/contacts"))
  (GET "/contacts" _
    (-> (query-contacts)
        contact-list-view
        html))
  (GET "/contacts/:id" [id :<< UUID/fromString]
    (-> (find-contact id)
        contact-view
        html)))

(def handler
  (wrap-reload #'app-routes))

(defonce server
  (run-jetty #'handler {:port 3000 :join? false}))
