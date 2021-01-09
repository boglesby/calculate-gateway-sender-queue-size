package example.client.domain;

import lombok.Data;
import lombok.NonNull;
import lombok.ToString;
import org.springframework.data.annotation.Id;
import org.springframework.data.gemfire.mapping.annotation.Region;

import java.math.BigDecimal;

@Data
@ToString
@Region("Trade")
public class Trade {

  @Id
  @NonNull
  private final String id;

  @NonNull
  private final String cusip;

  private final int shares;

  @NonNull
  private final BigDecimal price;

  @NonNull
  @ToString.Exclude
  private final byte[] payload;

  private final long createTime;

  private final long updateTime;

  @ToString.Include
  public int payloadLength() {
    return this.payload.length;
  }
}
