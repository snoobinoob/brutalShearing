package brutalShearing.patch;

import java.util.ArrayList;
import necesse.engine.modLoader.annotations.ModMethodPatch;
import necesse.entity.mobs.Attacker;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.MobWasHitEvent;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.friendly.HusbandryMob;
import necesse.entity.pickup.PickupEntity;
import necesse.inventory.InventoryItem;
import necesse.level.maps.Level;
import net.bytebuddy.asm.Advice;

public class SheepMobPatch {
    @ModMethodPatch(target = Mob.class, name = "isHit", arguments = {MobWasHitEvent.class, Attacker.class})
    public static class isHitPatch {
        @Advice.OnMethodEnter(skipOn = Advice.OnNonDefaultValue.class)
        public static boolean onEnter(@Advice.This Mob mob, @Advice.Argument(1) Attacker attacker) {
            if (!(mob instanceof HusbandryMob && attacker.getAttackOwner() instanceof PlayerMob)) {
                return false;
            }

            HusbandryMob husbandryMob = (HusbandryMob) mob;
            if (!husbandryMob.canShear(null)) {
                return false;
            }

            ArrayList<InventoryItem> products = new ArrayList<>();
            husbandryMob.onShear(null, products);
            Level mobLevel = husbandryMob.getLevel();
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
