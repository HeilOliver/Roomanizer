package at.fhv.roomix.persist.model;

import javax.persistence.*;
import java.util.Collection;
import java.util.Objects;

@Entity
@Table(name = "facility", schema = "roomix", catalog = "")
public class FacilityEntity {
    private int facilityId;
    private String description;
    private int additionalCharge;
    private Collection<RoomfacilityEntity> roomfacilitiesByFacilityId;

    @Id
    @Column(name = "FacilityID")
    public int getFacilityId() {
        return facilityId;
    }

    public void setFacilityId(int facilityId) {
        this.facilityId = facilityId;
    }

    @Basic
    @Column(name = "Description")
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Basic
    @Column(name = "AdditionalCharge")
    public int getAdditionalCharge() {
        return additionalCharge;
    }

    public void setAdditionalCharge(int additionalCharge) {
        this.additionalCharge = additionalCharge;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FacilityEntity that = (FacilityEntity) o;
        return facilityId == that.facilityId &&
                additionalCharge == that.additionalCharge &&
                Objects.equals(description, that.description);
    }

    @Override
    public int hashCode() {

        return Objects.hash(facilityId, description, additionalCharge);
    }

    @OneToMany(mappedBy = "facilityByFacility")
    public Collection<RoomfacilityEntity> getRoomfacilitiesByFacilityId() {
        return roomfacilitiesByFacilityId;
    }

    public void setRoomfacilitiesByFacilityId(Collection<RoomfacilityEntity> roomfacilitiesByFacilityId) {
        this.roomfacilitiesByFacilityId = roomfacilitiesByFacilityId;
    }
}