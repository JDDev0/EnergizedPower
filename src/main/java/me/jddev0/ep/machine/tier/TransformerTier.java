package me.jddev0.ep.machine.tier;

import me.jddev0.ep.block.EPBlocks;
import me.jddev0.ep.block.entity.EPBlockEntities;
import me.jddev0.ep.block.entity.TransformerBlockEntity;
import me.jddev0.ep.config.ModConfigs;
import me.jddev0.ep.screen.EPMenuTypes;
import me.jddev0.ep.screen.TransformerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;

public enum TransformerTier {
    LV, MV, HV, EHV;

    public MenuType<TransformerMenu> getMenuTypeFromTierAndType(TransformerType type) {
        return switch(this) {
            case LV -> switch(type) {
                case TYPE_1_TO_N -> EPMenuTypes.LV_TRANSFORMER_1_TO_N_MENU.get();
                case TYPE_3_TO_3 -> EPMenuTypes.LV_TRANSFORMER_3_TO_3_MENU.get();
                case TYPE_N_TO_1 -> EPMenuTypes.LV_TRANSFORMER_N_TO_1_MENU.get();
                case CONFIGURABLE -> EPMenuTypes.CONFIGURABLE_LV_TRANSFORMER_MENU.get();
            };
            case MV -> switch(type) {
                case TYPE_1_TO_N -> EPMenuTypes.MV_TRANSFORMER_1_TO_N_MENU.get();
                case TYPE_3_TO_3 -> EPMenuTypes.MV_TRANSFORMER_3_TO_3_MENU.get();
                case TYPE_N_TO_1 -> EPMenuTypes.MV_TRANSFORMER_N_TO_1_MENU.get();
                case CONFIGURABLE -> EPMenuTypes.CONFIGURABLE_MV_TRANSFORMER_MENU.get();
            };
            case HV -> switch(type) {
                case TYPE_1_TO_N -> EPMenuTypes.HV_TRANSFORMER_1_TO_N_MENU.get();
                case TYPE_3_TO_3 -> EPMenuTypes.HV_TRANSFORMER_3_TO_3_MENU.get();
                case TYPE_N_TO_1 -> EPMenuTypes.HV_TRANSFORMER_N_TO_1_MENU.get();
                case CONFIGURABLE -> EPMenuTypes.CONFIGURABLE_HV_TRANSFORMER_MENU.get();
            };
            case EHV -> switch(type) {
                case TYPE_1_TO_N -> EPMenuTypes.EHV_TRANSFORMER_1_TO_N_MENU.get();
                case TYPE_3_TO_3 -> EPMenuTypes.EHV_TRANSFORMER_3_TO_3_MENU.get();
                case TYPE_N_TO_1 -> EPMenuTypes.EHV_TRANSFORMER_N_TO_1_MENU.get();
                case CONFIGURABLE -> EPMenuTypes.CONFIGURABLE_EHV_TRANSFORMER_MENU.get();
            };
        };
    }

    public BlockEntityType<TransformerBlockEntity> getEntityTypeFromTierAndType(TransformerType type) {
        return switch(this) {
            case LV -> switch(type) {
                case TYPE_1_TO_N -> EPBlockEntities.LV_TRANSFORMER_1_TO_N_ENTITY.get();
                case TYPE_3_TO_3 -> EPBlockEntities.LV_TRANSFORMER_3_TO_3_ENTITY.get();
                case TYPE_N_TO_1 -> EPBlockEntities.LV_TRANSFORMER_N_TO_1_ENTITY.get();
                case CONFIGURABLE -> EPBlockEntities.CONFIGURABLE_LV_TRANSFORMER_ENTITY.get();
            };
            case MV -> switch(type) {
                case TYPE_1_TO_N -> EPBlockEntities.MV_TRANSFORMER_1_TO_N_ENTITY.get();
                case TYPE_3_TO_3 -> EPBlockEntities.MV_TRANSFORMER_3_TO_3_ENTITY.get();
                case TYPE_N_TO_1 -> EPBlockEntities.MV_TRANSFORMER_N_TO_1_ENTITY.get();
                case CONFIGURABLE -> EPBlockEntities.CONFIGURABLE_MV_TRANSFORMER_ENTITY.get();
            };
            case HV -> switch(type) {
                case TYPE_1_TO_N -> EPBlockEntities.HV_TRANSFORMER_1_TO_N_ENTITY.get();
                case TYPE_3_TO_3 -> EPBlockEntities.HV_TRANSFORMER_3_TO_3_ENTITY.get();
                case TYPE_N_TO_1 -> EPBlockEntities.HV_TRANSFORMER_N_TO_1_ENTITY.get();
                case CONFIGURABLE -> EPBlockEntities.CONFIGURABLE_HV_TRANSFORMER_ENTITY.get();
            };
            case EHV -> switch(type) {
                case TYPE_1_TO_N -> EPBlockEntities.EHV_TRANSFORMER_1_TO_N_ENTITY.get();
                case TYPE_3_TO_3 -> EPBlockEntities.EHV_TRANSFORMER_3_TO_3_ENTITY.get();
                case TYPE_N_TO_1 -> EPBlockEntities.EHV_TRANSFORMER_N_TO_1_ENTITY.get();
                case CONFIGURABLE -> EPBlockEntities.CONFIGURABLE_EHV_TRANSFORMER_ENTITY.get();
            };
        };
    }

    public Block getBlockFromTierAndType(TransformerType type) {
        return switch(this) {
            case LV -> switch(type) {
                case TYPE_1_TO_N -> EPBlocks.LV_TRANSFORMER_1_TO_N.get();
                case TYPE_3_TO_3 -> EPBlocks.LV_TRANSFORMER_3_TO_3.get();
                case TYPE_N_TO_1 -> EPBlocks.LV_TRANSFORMER_N_TO_1.get();
                case CONFIGURABLE -> EPBlocks.CONFIGURABLE_LV_TRANSFORMER.get();
            };
            case MV -> switch(type) {
                case TYPE_1_TO_N -> EPBlocks.MV_TRANSFORMER_1_TO_N.get();
                case TYPE_3_TO_3 -> EPBlocks.MV_TRANSFORMER_3_TO_3.get();
                case TYPE_N_TO_1 -> EPBlocks.MV_TRANSFORMER_N_TO_1.get();
                case CONFIGURABLE -> EPBlocks.CONFIGURABLE_MV_TRANSFORMER.get();
            };
            case HV -> switch(type) {
                case TYPE_1_TO_N -> EPBlocks.HV_TRANSFORMER_1_TO_N.get();
                case TYPE_3_TO_3 -> EPBlocks.HV_TRANSFORMER_3_TO_3.get();
                case TYPE_N_TO_1 -> EPBlocks.HV_TRANSFORMER_N_TO_1.get();
                case CONFIGURABLE -> EPBlocks.CONFIGURABLE_HV_TRANSFORMER.get();
            };
            case EHV -> switch(type) {
                case TYPE_1_TO_N -> EPBlocks.EHV_TRANSFORMER_1_TO_N.get();
                case TYPE_3_TO_3 -> EPBlocks.EHV_TRANSFORMER_3_TO_3.get();
                case TYPE_N_TO_1 -> EPBlocks.EHV_TRANSFORMER_N_TO_1.get();
                case CONFIGURABLE -> EPBlocks.CONFIGURABLE_EHV_TRANSFORMER.get();
            };
        };
    }

    public String getMachineNameFromTierAndType(TransformerType type) {
        return switch(this) {
            case LV -> switch(type) {
                case TYPE_1_TO_N -> "lv_transformer_1_to_n";
                case TYPE_3_TO_3 -> "lv_transformer_3_to_3";
                case TYPE_N_TO_1 -> "lv_transformer_n_to_1";
                case CONFIGURABLE -> "configurable_lv_transformer";
            };
            case MV -> switch(type) {
                case TYPE_1_TO_N -> "transformer_1_to_n";
                case TYPE_3_TO_3 -> "transformer_3_to_3";
                case TYPE_N_TO_1 -> "transformer_n_to_1";
                case CONFIGURABLE -> "configurable_mv_transformer";
            };
            case HV -> switch(type) {
                case TYPE_1_TO_N -> "hv_transformer_1_to_n";
                case TYPE_3_TO_3 -> "hv_transformer_3_to_3";
                case TYPE_N_TO_1 -> "hv_transformer_n_to_1";
                case CONFIGURABLE -> "configurable_hv_transformer";
            };
            case EHV -> switch(type) {
                case TYPE_1_TO_N -> "ehv_transformer_1_to_n";
                case TYPE_3_TO_3 -> "ehv_transformer_3_to_3";
                case TYPE_N_TO_1 -> "ehv_transformer_n_to_1";
                case CONFIGURABLE -> "configurable_ehv_transformer";
            };
        };
    }

    public int getMaxEnergyTransferFromTier() {
        return switch(this) {
            case LV -> ModConfigs.COMMON_LV_TRANSFORMERS_TRANSFER_RATE.getValue();
            case MV -> ModConfigs.COMMON_MV_TRANSFORMERS_TRANSFER_RATE.getValue();
            case HV -> ModConfigs.COMMON_HV_TRANSFORMERS_TRANSFER_RATE.getValue();
            case EHV -> ModConfigs.COMMON_EHV_TRANSFORMERS_TRANSFER_RATE.getValue();
        };
    }
}
