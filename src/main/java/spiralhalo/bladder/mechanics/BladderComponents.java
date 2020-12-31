package spiralhalo.bladder.mechanics;

import dev.onyxstudios.cca.api.v3.component.ComponentKey;
import dev.onyxstudios.cca.api.v3.component.ComponentRegistryV3;
import dev.onyxstudios.cca.api.v3.entity.EntityComponentFactoryRegistry;
import dev.onyxstudios.cca.api.v3.entity.EntityComponentInitializer;
import nerdhub.cardinal.components.api.util.RespawnCopyStrategy;
import net.minecraft.util.Identifier;
import spiralhalo.bladder.BladderMod;

public class BladderComponents implements EntityComponentInitializer {
    public static final ComponentKey<BladderComponent> BLADDER_POINT;

    static {
        final Identifier bladderComponentId = BladderMod.createId(BladderComponent.COMPONENT_ID);
        BLADDER_POINT = ComponentRegistryV3.INSTANCE.getOrCreate(bladderComponentId, BladderComponent.class);
    }

    @Override
    public void registerEntityComponentFactories(EntityComponentFactoryRegistry factoryRegistry) {
        factoryRegistry.registerForPlayers(BLADDER_POINT, BladderComponent::new, RespawnCopyStrategy.LOSSLESS_ONLY);
    }
}
