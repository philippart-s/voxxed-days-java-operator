# voxxed-days-java-operator
Source code for the Java operator talk at Voxxed Days Lux 2022

## 🎉 Init project
 - la branche `01-init-project` contient le résultat de cette étape
 - [installer / mettre](https://sdk.operatorframework.io/docs/installation/) à jour la dernière version du [Operator SDK](https://sdk.operatorframework.io/) (v1.20.1 au moment de l'écriture du readme)
 - créer le répertoire `voxxed-days-java-operator`
 - dans le répertoire `voxxed-days-java-operator`, scaffolding du projet avec Quarkus : `operator-sdk init --plugins quarkus --domain fr.wilda --project-name voxxed-days-java-operator`
 - l'arborescence générée est la suivante:
```bash
.
├── LICENSE
├── Makefile
├── PROJECT
├── README.md
├── pom.xml
├── src
│   └── main
│       ├── java
│       └── resources
│           └── application.properties
```
 - ⚠️ Au moment de l'écriture de ce tuto il est nécessaire de changer manuellement les versions de Quarkus et du SDK dans le `pom.xml`:
    - passer la propriété `quarkus.version` à `2.7.6.Final`
    - passer la propriété `quarkus-sdk.version` à `3.0.8`
 - supprimer le `-operator` dans le nom du fichier `application.properties`:
```yaml
    quarkus.container-image.build=true
    #quarkus.container-image.group=
    quarkus.container-image.name=voxxed-days-java-operator
    # set to true to automatically apply CRDs to the cluster when they get regenerated
    quarkus.operator-sdk.crd.apply=false
    # set to true to automatically generate CSV from your code
    quarkus.operator-sdk.generate-csv=false
```
 - vérification que cela compile : `mvn clean compile`
 - tester le lancement: `mvn quarkus:dev`:
```bash
__  ____  __  _____   ___  __ ____  ______ 
 --/ __ \/ / / / _ | / _ \/ //_/ / / / __/ 
 -/ /_/ / /_/ / __ |/ , _/ ,< / /_/ /\ \   
--\___\_\____/_/ |_/_/|_/_/|_|\____/___/   
             Powered by Quarkus 2.7.5.Final
2022-06-07 17:44:58,528 WARN  [io.fab.kub.cli.Config] (Quarkus Main Thread) Found multiple Kubernetes config files [[/Users/stef/Config/k8s/ovh-example-2.yml, /Users/stef/.kube/config]], using the first one: [/Users/stef/Config/k8s/ovh-example-2.yml]. If not desired file, please change it by doing `export KUBECONFIG=/path/to/kubeconfig` on Unix systems or `$Env:KUBECONFIG=/path/to/kubeconfig` on Windows.

2022-06-07 17:44:58,586 WARN  [io.fab.kub.cli.Config] (Quarkus Main Thread) Found multiple Kubernetes config files [[/Users/stef/Config/k8s/ovh-example-2.yml, /Users/stef/.kube/config]], using the first one: [/Users/stef/Config/k8s/ovh-example-2.yml]. If not desired file, please change it by doing `export KUBECONFIG=/path/to/kubeconfig` on Unix systems or `$Env:KUBECONFIG=/path/to/kubeconfig` on Windows.
2022-06-07 17:44:58,776 INFO  [io.qua.ope.run.AppEventListener] (Quarkus Main Thread) Quarkus Java Operator SDK extension 3.0.7 (commit: 22fed83 on branch: 22fed8391b7b153616bd79c5f829cdd8a7edd5bd) built on Thu Apr 07 16:13:21 CEST 2022
2022-06-07 17:44:58,776 WARN  [io.qua.ope.run.AppEventListener] (Quarkus Main Thread) No Reconciler implementation was found so the Operator was not started.
2022-06-07 17:44:58,816 INFO  [io.quarkus] (Quarkus Main Thread) voxxed-days-java-operator 0.0.1-SNAPSHOT on JVM (powered by Quarkus 2.7.5.Final) started in 1.827s. Listening on: http://localhost:8080
2022-06-07 17:44:58,817 INFO  [io.quarkus] (Quarkus Main Thread) Profile dev activated. Live Coding activated.
2022-06-07 17:44:58,817 INFO  [io.quarkus] (Quarkus Main Thread) Installed features: [cdi, kubernetes, kubernetes-client, micrometer, openshift-client, operator-sdk, smallrye-context-propagation, smallrye-health, vertx]
```

## 📄 CRD generation
 - la branche `02-crd-generation` contient le résultat de cette étape
 - création de l'API : `operator-sdk create api --version v1 --kind NginxOperator`
 - cette commande a créé les 4 classes nécessaires pour créer l'opérateur:
```bash
src
└── main
    ├── java
    │   └── wilda
    │       └── fr
    │           ├── NginxOperator.java
    │           ├── NginxOperatorReconciler.java
    │           ├── NginxOperatorSpec.java
    │           └── NginxOperatorStatus.java
```
  - tester que tout compile que la CRD se génère bien: `mvn clean package` (ou restez en mode `mvn quarkus:dev` pour voir la magie opérer en direct :wink:)
  - une exception apparaît, cela vient du fait que la CRD n'est pas générée côté Kubernetes, cela va être corrigée dans les étapes suivantes:
```bash
2022-03-28 15:42:02,261 ERROR [io.qua.run.Application] (Quarkus Main Thread) Failed to start application (with profile dev): io.javaoperatorsdk.operator.MissingCRDException: 'nginxoperators.fr.wilda' v1 CRD was not found on the cluster, controller 'nginxoperatorreconciler' cannot be registered
```
  - la CRD doit être générée dans le target, `target/kubernetes/nginxoperators.fr.wilda-v1.yml`:
      ```yaml
      # Generated by Fabric8 CRDGenerator, manual edits might get overwritten!
      apiVersion: apiextensions.k8s.io/v1
      kind: CustomResourceDefinition
      metadata:
        name: nginxoperators.fr.wilda
      spec:
        group: fr.wilda
        names:
          kind: NginxOperator
          plural: nginxoperators
          singular: nginxoperator
        scope: Namespaced
        versions:
        - name: v1
          schema:
            openAPIV3Schema:
              properties:
                spec:
                  type: object
                status:
                  type: object
              type: object
          served: true
          storage: true
          subresources:
            status: {}
      ```
