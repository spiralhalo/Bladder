package spiralhalo.bladder.mechanics;

import dev.onyxstudios.cca.api.v3.component.ComponentKey;
import dev.onyxstudios.cca.api.v3.component.ComponentRegistryV3;
import dev.onyxstudios.cca.api.v3.entity.EntityComponentFactoryRegistry;
import dev.onyxstudios.cca.api.v3.entity.EntityComponentInitializer;
import nerdhub.cardinal.components.api.util.RespawnCopyStrategy;
import spiralhalo.bladder.BladderMod;

public class BladderComponents implements EntityComponentInitializer {
    public static final ComponentKey<BladderComponent> BLADDER_POINT =
            ComponentRegistryV3.INSTANCE.getOrCreate(BladderMod.createId(BladderComponent.COMPONENT_ID), BladderComponent.class);

    @Override
    public void registerEntityComponentFactories(EntityComponentFactoryRegistry factoryRegistry) {
        factoryRegistry.registerForPlayers(BLADDER_POINT, player -> new BladderComponent(player), RespawnCopyStrategy.LOSSLESS_ONLY);
    }
}
