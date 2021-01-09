package example.server.function;

import org.apache.geode.cache.Cache;
import org.apache.geode.cache.CacheFactory;
import org.apache.geode.cache.Declarable;

import org.apache.geode.cache.execute.Function;
import org.apache.geode.cache.execute.FunctionContext;

import org.apache.geode.cache.wan.GatewaySender;

public class CalculateGatewaySenderQueueEntrySizesFunction implements Function, Declarable {
  
  private final Cache cache;

  public CalculateGatewaySenderQueueEntrySizesFunction() {
    this.cache = CacheFactory.getAnyInstance();
  }

  @Override
  public void execute(FunctionContext context) {
    // Get the arguments
    Object[] arguments = (Object[]) context.getArguments();
    String senderIdsArg = (String) arguments[0];
    String[] senderIds = senderIdsArg.split(",");
    boolean summaryOnly = (Boolean) arguments[1];
    boolean groupByBucket = (Boolean) arguments[2];

    // Iterate the sender ids
    for (String senderId : senderIds) {
      // Get the GatewaySender for the sender id
      GatewaySender sender = this.cache.getGatewaySender(senderId);
      
      // Process the GatewaySender
      if (sender == null) {
        this.cache.getLogger().warning("GatewaySender " + senderId + " doesn't exist");
      } else {
        getSizer(sender, groupByBucket).calculateEntrySizes(summaryOnly);
      }
    }

    // Send the response
    context.getResultSender().lastResult(true);
  }
  
  private GatewaySenderQueueEntrySizer getSizer(GatewaySender sender, boolean groupByBucket) {
    GatewaySenderQueueEntrySizer logger = null;
    if (sender.isParallel()) {
      logger = groupByBucket
        ? new ParallelGatewaySenderQueueByBucketEntrySizer(this.cache, sender)
        : new ParallelGatewaySenderQueueEntrySizer(this.cache, sender);
    } else {
      logger = new SerialGatewaySenderQueueEntrySizer(this.cache, sender);
    }
    return logger;
  }

  @Override
  public String getId() {
    return getClass().getSimpleName();
  }
}
