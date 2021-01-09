package example.server.function;

import org.apache.geode.cache.Cache;

import org.apache.geode.cache.wan.GatewaySender;

import org.apache.geode.internal.cache.BucketRegion;
import org.apache.geode.internal.cache.NonTXEntry;
import org.apache.geode.internal.cache.PartitionedRegion;

import org.apache.geode.internal.cache.wan.parallel.ParallelGatewaySenderQueue;

import java.util.List;
import java.util.Set;

import java.util.stream.Collectors;

public class ParallelGatewaySenderQueueEntrySizer extends AbstractParallelGatewaySenderQueueEntrySizer {

  public ParallelGatewaySenderQueueEntrySizer(Cache cache, GatewaySender sender) {
    super(cache, sender);
  }

  public void calculateEntrySizes(boolean summaryOnly) {
    // Get the region implementing the queue
    PartitionedRegion region = (PartitionedRegion) this.cache.getRegion(sender.getId() + ParallelGatewaySenderQueue.QSTRING);
    StringBuilder builder = new StringBuilder();

    // Calculate the sizes of the primary queue entries
    List<NonTXEntry> primaryEntries = getEntries(region.getDataStore().getAllLocalPrimaryBucketRegions());
    long primaryBytes = addAndReturnSizes(builder, primaryEntries, summaryOnly, true);

    // Calculate the sizes of the secondary queue entries
    List<NonTXEntry> secondaryEntries = getEntries(getLocalSecondaryBucketRegions(region));
    long secondaryBytes = addAndReturnSizes(builder, secondaryEntries, summaryOnly, false);

    // Add the summary
    addSummary(builder, primaryEntries.size(), primaryBytes, secondaryEntries.size(), secondaryBytes);

    // Log the results
    this.cache.getLogger().info(builder.toString());
  }

  private List<NonTXEntry> getEntries(Set<BucketRegion> brs) {
    return (List<NonTXEntry>) brs
      .stream()
      .flatMap(br -> br.entrySet().stream())
      .collect(Collectors.toList());
  }

  private long addAndReturnSizes(StringBuilder builder, List<NonTXEntry> entries, boolean summaryOnly, boolean isPrimary) {
    if (!summaryOnly) {
      addHeader(builder, entries, isPrimary);
    }

    long totalBytes = entries
      .stream()
      .map(entry -> entry.getRegionEntry())
      .sorted((entry1, entry2) -> Long.compare((long) entry1.getKey(), (long) entry2.getKey()))
      .mapToLong(entry -> addAndReturnSize(builder, entry, summaryOnly))
      .sum();

    if (!summaryOnly) {
      builder.append("\n");
    }
    return totalBytes;
  }

  private void addHeader(StringBuilder builder, List<NonTXEntry> entries, boolean isPrimary) {
    builder
      .append("\nParallel GatewaySender ")
      .append(this.sender.getId())
      .append(" contains the following ")
      .append(format.format(entries.size()))
      .append(isPrimary ? " primary" : " secondary")
      .append(" entries and sizes:\n");
  }
}
