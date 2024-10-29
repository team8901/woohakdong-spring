package woohakdong.server.domain.item;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import woohakdong.server.domain.ItemHistory.ItemHistory;
import woohakdong.server.domain.club.Club;
import woohakdong.server.domain.clubAccountHistory.ClubAccountHistory;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class Item {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long itemId;

    @Column(nullable = false)
    private String itemName;

    @Column(nullable = false)
    private String itemPhoto;

    @Column(nullable = false)
    private Boolean itemAvailable;

    @Column(nullable = false)
    private Boolean itemUsing;

    private String itemDescription;
    private Integer itemRentalTime;
    private String itemLocation;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ItemCategory itemCategory;

    @Column(nullable = false)
    private Integer itemRentalMaxDay;

    private LocalDateTime itemRentalDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "club_id")
    private Club club;

    @OneToMany(mappedBy = "item", cascade = CascadeType.ALL)
    private List<ItemHistory> itemHistories;

    @Builder
    public Item(Club club, LocalDateTime itemRentalDate, Integer itemRentalTime, String itemLocation,
                ItemCategory itemCategory, Integer itemRentalMaxDay, Boolean itemAvailable,
                Boolean itemUsing, String itemDescription, String itemPhoto, String itemName) {
        this.club = club;
        this.itemRentalDate = itemRentalDate;
        this.itemRentalTime = itemRentalTime;
        this.itemLocation = itemLocation;
        this.itemCategory = itemCategory;
        this.itemRentalMaxDay = itemRentalMaxDay;
        this.itemAvailable = itemAvailable;
        this.itemUsing = itemUsing;
        this.itemDescription = itemDescription;
        this.itemPhoto = itemPhoto;
        this.itemName = itemName;
    }

    public void setBorrow(Boolean itemUsing, LocalDateTime itemRentalDate, Integer itemRentalTime) {
        this.itemUsing = itemUsing;
        this.itemRentalDate = itemRentalDate;
        this.itemRentalTime = itemRentalTime;
    }

    public void setItemUsing(Boolean itemUsing) {
        this.itemUsing = itemUsing;
    }

    public void setItemAvailable(Boolean itemAvailable) {
        this.itemAvailable = itemAvailable;
    }

    public void updateItem(String itemName, String itemPhoto, String itemDescription,
                           String itemLocation, ItemCategory itemCategory, Integer itemRentalMaxDay) {
        this.itemName = itemName;
        this.itemPhoto = itemPhoto;
        this.itemDescription = itemDescription;
        this.itemLocation = itemLocation;
        this.itemCategory = itemCategory;
        this.itemRentalMaxDay = itemRentalMaxDay;
    }
}
