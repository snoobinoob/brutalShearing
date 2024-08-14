package brutalShearing.patch;

import java.util.ArrayList;
import necesse.engine.modLoader.annotations.ModMethodPatch;
import necesse.entity.mobs.Attacker;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.MobWasHitEvent;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.friendly.SheepMob;
import necesse.entity.pickup.PickupEntity;
import necesse.inventory.InventoryItem;
import necesse.level.maps.Level;
import net.bytebuddy.asm.Advice;

public class SheepMobPatch {
    @ModMethodPatch(target = Mob.class, name = "isHit", arguments = {MobWasHitEvent.class, Attacker.class})
    public static class isHitPatch {
        @Advice.OnMethodEnter(skipOn = Advice.OnNonDefaultValue.class)
        public static boolean onEnter(@Advice.This Mob mob, @Advice.Argument(1) Attacker attacker) {
            if (!(mob instanceof SheepMob && attacker.getAttackOwner() instanceof PlayerMob)) {
                return false;
            }

            SheepMob sheep = (SheepMob) mob;
            if (!sheep.canShear(null)) {
                return false;
            }

            ArrayList<InventoryItem> products = new ArrayList<>();
            sheep.onShear(null, products);
            Level mobLevel = sheep.getLevel();
            if (mobLevel.isServer()) {
                for (InventoryItem product : products) {
                    PickupEntity pickup = product.getPickupEntity(mobLevel, mob.x, mob.y);
                    mobLevel.entityManager.pickups.add(pickup);
                }
            }
            return true;
        }
    }
}
