package org.arcticquests.dev.oneironaut.oneironautt.recipe;

import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;

public class CopyProperties {
    @SuppressWarnings({"rawtypes", "unchecked"})
    public static BlockState copyProperties(BlockState original, BlockState copyTo) {
        for (Property prop : original.getProperties()) {
            if (copyTo.hasProperty(prop)) {
                copyTo = copyTo.setValue(prop, original.getValue(prop));
            }
        }

        return copyTo;
    }
}
