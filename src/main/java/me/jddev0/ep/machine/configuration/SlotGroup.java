package me.jddev0.ep.machine.configuration;

import java.util.List;

public class SlotGroup {
    public static SlotGroup of(SlotEntry... slots) {
        return new SlotGroup(slots);
    }

    public static SlotGroup of(List<SlotEntry> slots) {
        return new SlotGroup(slots);
    }

    private final SlotMode mode;
    private final List<SlotEntry> slots;

    public SlotGroup(SlotEntry... slots) {
        this(List.of(slots));
    }

    public SlotGroup(List<SlotEntry> slots) {
        if(slots.isEmpty())
            throw new IllegalArgumentException("Slot count must be > 0");

        this.slots = List.copyOf(slots);

        this.mode = this.slots.stream().skip(1).map(SlotEntry::mode).reduce(this.slots.getFirst().mode(), (modeA, modeB) -> {
            if(modeA == modeB)
                return modeA;

            return SlotMode.BOTH;
        });
    }

    public SlotMode getMode() {
        return mode;
    }

    public List<SlotEntry> getSlots() {
        return slots;
    }
}
