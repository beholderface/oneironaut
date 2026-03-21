package org.arcticquests.dev.oneironaut.oneironautt.casting.environments;

import at.petrak.hexcasting.api.casting.ParticleSpray;
import at.petrak.hexcasting.api.casting.eval.CastResult;
import at.petrak.hexcasting.api.casting.eval.env.PackagedItemCastEnv;
import at.petrak.hexcasting.api.casting.iota.Iota;
import at.petrak.hexcasting.api.casting.iota.ListIota;
import at.petrak.hexcasting.api.casting.iota.NullIota;
import at.petrak.hexcasting.api.pigment.FrozenPigment;
import at.petrak.hexcasting.common.lib.hex.HexEvalSounds;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.phys.Vec3;

public class ReverbRodCastEnv extends PackagedItemCastEnv {
    public ReverbRodCastEnv(ServerPlayer caster, InteractionHand castingHand, boolean active) {
        super(caster, castingHand);
        this.timestamp = caster.level().getGameTime();
        this.initialLook = caster.getLookAngle();
        this.initialPos = caster.getEyePosition();
        this.delay = 0;
        this.resetCooldown = 20;
        this.currentlyCasting = active;
        this.castInProgress = false;
        this.storedIota = new NullIota();
    }
    private final long timestamp;
    private final Vec3 initialLook;
    private final Vec3 initialPos;
    private int delay;
    private int resetCooldown;
    private boolean currentlyCasting;
    //is a pattern list currently being processed?
    private boolean castInProgress;
    private Iota storedIota;
    public long lastSoundTimestamp = 0;
    @Override
    public void postExecution(CastResult result) {
        super.postExecution(result);
        if (result.getSound().priority() >= HexEvalSounds.SPELL.priority()){
            this.sound = this.sound.greaterOf(result.getSound());
        }
    }

    @Override
    public void produceParticles(ParticleSpray particles, FrozenPigment pigment) {
        if ((!this.caster.getBoundingBox().contains(particles.component1())) || this.world.getGameTime() % 30 == 0){
            particles.sprayParticles(this.world, pigment);
        }
    }

    public long getLastSoundTimestamp(){
        return this.lastSoundTimestamp;
    }
    public void updateLastSoundtimestamp(){
        this.lastSoundTimestamp = this.world.getGameTime();
    }

    public long getTimestamp(){
        return this.timestamp;
    }

    public Vec3 getInitialPos(){
        return this.initialPos;
    }

    public Vec3 getInitialLook(){
        return this.initialLook;
    }

    public int getDelay(){
        return this.delay;
    }
    public int setDelay(int newDelay){
        //why would you ever want an hour of delay between casts? and negative delay doesn't make sense, it's not like you can schedule a cast in the past
        int boundedDelay = Math.max(0, Math.min(20 * 60 * 60, newDelay));
        this.delay = boundedDelay;
        return boundedDelay - newDelay;
    }
    public int adjustDelay(int delta){
        return this.setDelay(this.getDelay() + delta);
    }

    public int getResetCooldown(){
        return this.resetCooldown;
    }
    public int setResetCooldown(int cooldown){
        int boundedCooldown = Math.max(0, Math.min(20 * 60 * 60, cooldown));
        this.resetCooldown = boundedCooldown;
        return boundedCooldown - cooldown;
    }
    public boolean getCurrentlyCasting() {
        return currentlyCasting;
    }

    public void setCurrentlyCasting(boolean newBool) {
        this.currentlyCasting = newBool;
    }

    public void stopCasting(){
        this.currentlyCasting = false;
    }

    public Iota getStoredIota(){
        return this.storedIota;
    }
    public boolean setStoredIota(Iota newIota){
        if (newIota.getType().equals(ListIota.TYPE)){
            return false;
        }
        this.storedIota = newIota;
        return true;
    }
    public void setCastInProgress(boolean newValue){
        this.castInProgress = newValue;
    }
    public boolean getCastingInProgress(){
        return this.castInProgress;
    }
}
