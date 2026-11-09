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

    /**
     * Optimized can input lookup for the first 64 slots
     */
    protected final long canInputBitField;
    /**
     * Optimized can output lookup for the first 64 slots
     */
    protected final long canOutputBitField;

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

        long canInputBitField = 0;
        long canOutputBitField = 0;
        for(SlotEntry slot:this.slots) {
            if(slot.index() >= 64)
                continue;

            canInputBitField |= (slot.mode().canInput()?1L:0L) << slot.index();
            canOutputBitField |= (slot.mode().canOutput()?1L:0L) << slot.index();
        }

        this.canInputBitField = canInputBitField;
        this.canOutputBitField = canOutputBitField;
    }

    public SlotMode getMode() {
        return mode;
    }

    public List<SlotEntry> getSlots() {
        return slots;
    }
}
