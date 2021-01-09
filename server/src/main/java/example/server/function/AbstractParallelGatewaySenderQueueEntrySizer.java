package example.server.function;

import org.apache.geode.cache.Cache;
import org.apache.geode.cache.wan.GatewaySender;
import org.apache.geode.internal.cache.BucketRegion;
import org.apache.geode.internal.cache.PartitionedRegion;

import java.util.HashSet;
import java.util.Set;

public abstract class AbstractParallelGatewaySenderQueueEntrySizer extends AbstractGatewaySenderQueueEntrySizer {

  public AbstractParallelGatewaySenderQueueEntrySizer(Cache cache, GatewaySender sender) {
    super(cache, sender);
  }

  protected void addSummary(StringBuilder builder, int primaryEntries, long primaryBytes, int secondaryEntries, long secondaryBytes) {
    builder
      .append("\nParallel GatewaySender ")
      .append(this.sender.getId())
      .append(" contains:\n")
      .append("\n\t")
      .append(format.format(primaryEntries))
      .append(" primary entries consisting of ")
      .append(format.format(primaryBytes))
      .append(" bytes")
      .append("\n\t")
      .append(format.format(secondaryEntries))
      .append(" secondary entries consisting of ")
      .append(format.format(secondaryBytes))
      .append(" bytes")
      .append("\n\t")
      .append(format.format(primaryEntries + secondaryEntries))
      .append(" total entries consisting of ")
      .append(format.format(primaryBytes + secondaryBytes))
      .append(" bytes");
  }

  protected Set<BucketRegion> getLocalSecondaryBucketRegions(PartitionedRegion region) {
    Set<BucketRegion> primaryBucketRegions = region.getDataStore().getAllLocalPrimaryBucketRegions();
    Set<BucketRegion> allBucketRegions = new HashSet<>(region.getDataStore().getAllLocalBucketRegions());
    allBucketRegions.removeAll(primaryBucketRegions);
    return allBucketRegions;
  }
}
