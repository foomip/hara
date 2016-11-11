(ns documentation.hara-security)

[[:chapter {:title "Introduction"}]]

"
[hara.security](https://github.com/zcaudate/hara/blob/master/src/hara/security.clj) provides an intuitive interface around the JCA suite of tools for securing your applications"

[[:section {:title "Installation"}]]

"
Add to `project.clj` dependencies:

    [im.chit/hara.security \"{{PROJECT.version}}\"]
    
All functionality is found contained in the `hara.security` namespace"

(comment (require '[hara.security :as security]))

[[:section {:title "Motivation"}]]

"`hara.security` aims to integrate easily with the JSE/JCA framework. As many other libraries such as Bouncy Castle also extend this framework, the methods allow better exploration and usage of the options. Furthermore, deliberate effort was made to allow keys to be expressed as data so that encryption could be handled more explictly and with more understanding."

[[:chapter {:title "Index"}]]

[[:api {:namespace "hara.security" 
        :title ""
        :display #{:tags}}]]

[[:chapter {:title "API"}]]

[[:section {:title "Providers"}]]

[[:api {:namespace "hara.security" 
        :title ""
        :only ["list-providers"
               "list-services"
               "cipher"
               "key-generator"
               "key-pair-generator"
               "key-store"
               "mac"
               "message-digest"
               "signature"]}]]

[[:section {:title "Keys"}]]

[[:api {:namespace "hara.security" 
        :title ""
        :only ["generate-key"
               "generate-key-pair"
               "->key"
               "key->map"]}]]

[[:section {:title "Encryption"}]]

[[:api {:namespace "hara.security" 
        :title ""
        :only ["encrypt"
               "decrypt"]}]]

[[:section {:title "Digest"}]]

[[:api {:namespace "hara.security" 
        :title ""
        :only ["digest"
               "hmac"]}]]

[[:section {:title "Signature"}]]

[[:api {:namespace "hara.security" 
        :title ""
        :only ["sign"
               "verify"]}]]
