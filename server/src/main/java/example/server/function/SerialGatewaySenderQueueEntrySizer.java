package example.server.function;

import org.apache.geode.cache.Cache;
import org.apache.geode.cache.Region;

import org.apache.geode.cache.wan.GatewaySender;

import org.apache.geode.internal.cache.NonTXEntry;
import org.apache.geode.internal.cache.wan.InternalGatewaySender;

import java.util.Comparator;
import java.util.List;
import java.util.Set;

import java.util.stream.Collectors;

public class SerialGatewaySenderQueueEntrySizer extends AbstractGatewaySenderQueueEntrySizer {
  
  public SerialGatewaySenderQueueEntrySizer(Cache cache, GatewaySender sender) {
    super(cache, sender);
  }

  public void calculateEntrySizes(boolean summaryOnly) {
    // Calculate the sizes of the queue entries
    StringBuilder builder = new StringBuilder();
    long numBytes = addAndReturnSizes(builder, summaryOnly);

    // Add the summary
    addSummary(builder, numBytes);

    // Log the results
    this.cache.getLogger().info(builder.toString());
  }

  private long addAndReturnSizes(StringBuilder builder, boolean summaryOnly) {
    if (!summaryOnly) {
      addHeader(builder);
    }

    return getRegions()
      .stream()
      .mapToLong(region -> addAndReturnSizes(builder, region, summaryOnly))
      .sum();
  }

  private void addHeader(StringBuilder builder) {
    InternalGatewaySender igs = (InternalGatewaySender) this.sender;
    builder
      .append("\nSerial GatewaySender ")
      .append(this.sender.getId())
      .append(" contains the following ")
      .append(igs.getEventQueueSize())
      .append(igs.isPrimary() ? " primary" : " secondary")
      .append(" entries and sizes grouped by dispatcher:\n");
  }

  private List<Region> getRegions() {
    return ((InternalGatewaySender) this.sender).getQueues()
      .stream()
      .map(rq -> rq.getRegion())
      .sorted(Comparator.comparing(Region::getName))
      .collect(Collectors.toList());
  }

  private long addAndReturnSizes(StringBuilder builder, Region region, boolean summaryOnly) {
    if (!summaryOnly) {
      addHeader(builder, region);
    }

    long totalBytes = ((Set<NonTXEntry>) region.entrySet())
      .stream()
      .map(entry -> entry.getRegionEntry())
      .sorted((entry1, entry2) -> Long.compare((long) entry1.getKey(), (long) entry2.getKey()))
      .mapToLong(entry -> addAndReturnSize(builder, entry, summaryOnly))
      .sum();

    if (!summaryOnly) {
      builder
        .append("\n\n\tDispatcher ")
        .append(region.getName().replace("_SERIAL_GATEWAY_SENDER_QUEUE", ""))
        .append(" contains ")
        .append(format.format(totalBytes))
        .append(" total bytes")
        .append("\n");
    }
    return totalBytes;
  }

  private void addHeader(StringBuilder builder, Region region) {
    builder
      .append("\n\tDispatcher ")
      .append(region.getName().replace("_SERIAL_GATEWAY_SENDER_QUEUE", ""))
      .append(" contains the following ")
      .append(region.size())
      .append(" entries:\n");
  }

  private void addSummary(StringBuilder builder, long numBytes) {
    InternalGatewaySender igs = (InternalGatewaySender) this.sender;
    builder
      .append("\nSerial GatewaySender ")
      .append(sender.getId())
      .append(" contains ")
      .append(igs.getEventQueueSize())
      .append(igs.isPrimary() ? " primary" : " secondary")
      .append(" entries entries consisting of ")
      .append(format.format(numBytes))
      .append(" bytes");
  }
}
