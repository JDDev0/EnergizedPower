package me.jddev0.ep.machine.configuration;

public record SlotEntry(SlotMode mode, int index) {
    public static SlotEntry of(SlotMode mode, int index) {
        return new SlotEntry(mode, index);
    }

    public static SlotEntry ofInput(int index) {
        return new SlotEntry(SlotMode.INPUT, index);
    }

    public static SlotEntry ofOutput(int index) {
        return new SlotEntry(SlotMode.OUTPUT, index);
    }

    public static SlotEntry ofBoth(int index) {
        return new SlotEntry(SlotMode.BOTH, index);
    }
}
