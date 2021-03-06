(ns centipair.core.contrib.mail
  (:use postal.core
        centipair.core.settings
        centipair.core.secret
        selmer.parser))

;;email settings should be defined in centipair.core.secret
;;(def email-settings {:host "smtphost.server.com"
;;                     :user "username"
;;                     :pass "password"
;;                     :port 123})
;;

(defn send-registration-email [registration-request]
  (let [site-data site-settings
        email-body (render-file "email/registration.html" 
                                (assoc site-data :registration-key (str (:registration_key registration-request))))]
    (send-message email-settings
                  {:from (:email site-data)
                   :to (:email registration-request)
                   :subject (str "Please activate your " (:name site-data) " account")
                   :body [{:type "text/html"
                           :content email-body}]})))

(defn send-password-reset-email [params]
  (let [site-data site-settings
        email-body (render-file "email/password-reset.html" 
                                (assoc site-data :reset-key (:password-reset-key params)))]
    (send-message email-settings
                  {:from (:email site-data)
                   :to (:email params)
                   :subject (str "Reset your " (:name site-data) " password")
                   :body [{:type "text/html"
                           :content email-body}]})))


(defn send-test-email [mail]
  (println "sending test email")
  (send-message email-settings
                {:from "test@centipair.com"
                 :to "devasiajosephtest@gmail.com"
                 :subject (str "testing email number - " (:body mail))
                 :body (str "test email number -" (:body mail) )}))


(defn send-mail [mail]
  (println "sending email")
  (case (:purpose mail)
    "registration" (send-registration-email (:params mail))
    "test" (send-test-email (:params mail))))
