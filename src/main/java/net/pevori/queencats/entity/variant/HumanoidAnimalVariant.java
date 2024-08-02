package net.pevori.queencats.entity.variant;

public interface HumanoidAnimalVariant {
    int id = 0;

    default int getId() {
        return this.id;
    }

    static HumanoidAnimalVariant byId(int id) {
        return null;
    }
}
