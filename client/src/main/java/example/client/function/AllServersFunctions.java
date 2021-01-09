package example.client.function;

import org.springframework.data.gemfire.function.annotation.FunctionId;
import org.springframework.data.gemfire.function.annotation.OnServers;

@OnServers(resultCollector = "allServersResultCollector")
public interface AllServersFunctions {

  @FunctionId("CalculateGatewaySenderQueueEntrySizesFunction")
  Object calculateGatewaySenderQueueEntrySizes(String gatewaySenderId, boolean summaryOnly, boolean groupByBucket);
}
