A helm chart for the downloads API, to simplify deployment to Kubernetes.

It is recommended to use the official KryptonMC helm repository to deploy this chart.

The chart repository can be added like so:
```bash
helm repo add kryptonmc https://helm.kryptonmc.org
helm repo update
```

Then, to install and deploy the chart:
```bash
helm install downloads-api kryptonmc/downloads-api
```
