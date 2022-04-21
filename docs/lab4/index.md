# GitOps deployment with Day 2 operations



1. Prepare the ArgoCD app and project: Each student will have his/her own project within ArgoCD.

    * **Automatic way:**

    ```sh
    # same exported variables as before
    make prepare-argocd
    ```

    * Manual way: update the namespace, project, and repoURL elements in the `argocd/*.yaml` files.

1. Commit and push your changes to your gitops repository

    ```sh
    git commit -am "update configuration for my student id"
    git push 
    ```

1. Bootstrap Argocd:  

    ```sh
    make argocd
    # Or use 
    oc apply -f
    ```

1. To get the `admin` user's password use the command

    ```sh
    oc extract secret/openshift-gitops-cluster -n openshift-gitops --to=-
    ```

1. Get the ArgoCD User Interface URL and open a web browser

   ```sh
   chrome https://$(oc get route openshift-gitops-server -o jsonpath='{.status.ingress[].host}'  -n openshift-gitops)
   ```
