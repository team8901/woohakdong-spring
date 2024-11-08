package woohakdong.server.domain.itemBorrowed;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import woohakdong.server.domain.club.Club;
import woohakdong.server.domain.clubmember.ClubMember;
import woohakdong.server.domain.item.Item;

import java.time.LocalDateTime;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class ItemBorrowed {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long itemBorrowedId;

    private LocalDateTime itemBorrowedReturnDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "club_member_id")
    private ClubMember clubMember;

    @OneToOne
    @JoinColumn(name = "item_id")
    private Item item;

    @Builder
    private ItemBorrowed(ClubMember clubMember, Item item, LocalDateTime itemBorrowedReturnDate) {
        this.clubMember = clubMember;
        this.item = item;
        this.itemBorrowedReturnDate = itemBorrowedReturnDate;
    }

    public static ItemBorrowed createItemBorrowed(ClubMember clubMember, Item item, LocalDateTime itemBorrowedReturnDate) {
        ItemBorrowed itemBorrowed = ItemBorrowed.builder()
                .itemBorrowedReturnDate(itemBorrowedReturnDate)
                .clubMember(clubMember)
                .item(item)
                .build();
        return itemBorrowed;
    }
}
