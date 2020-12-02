# Run on a Spark standalone cluster in client deploy mode
../../../spark-3.0.0-bin-hadoop3.2/bin/spark-submit.cmd \
  --class scala.Test \
  --master spark://192.168.80.113:29092 \
  --deploy-mode cluster \
  --executor-memory 2G \
  --total-executor-cores 2 \
  build\libs\Spark-Test-v01-all.scala