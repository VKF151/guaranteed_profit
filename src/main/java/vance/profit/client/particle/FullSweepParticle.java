package vance.profit.client.particle;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.*;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.util.RandomSource;
import org.jspecify.annotations.NonNull;

@Environment(EnvType.CLIENT)
public class FullSweepParticle extends SingleQuadParticle {
    private final SpriteSet sprites;

    private FullSweepParticle(final ClientLevel level, final double x, final double y, final double z, final double size, final SpriteSet sprites) {
        super(level, x, y, z, (double)0.0F, (double)0.0F, (double)0.0F, sprites.first());
        this.sprites = sprites;
        this.lifetime = 4;
        float col = this.random.nextFloat() * 0.6F + 0.4F;
        this.rCol = col;
        this.gCol = col;
        this.bCol = col;
        this.quadSize = 1.75F - (float)size * 0.5F;
        this.setSpriteFromAge(sprites);
    }

    public int getLightCoords(final float a) {
        return 15728880;
    }


    @Override
    public @NonNull FacingCameraMode getFacingCameraMode() {
        return (target, camera, partialTickTime) -> {
            target.rotationXYZ((float) -Math.PI/2.0F, 0.0F, (float) Math.toRadians(-camera.yRot()));
        };
    }

    public void tick() {
        this.xo = this.x;
        this.yo = this.y;
        this.zo = this.z;
        if (this.age++ >= this.lifetime) {
            this.remove();
        } else {
            this.setSpriteFromAge(this.sprites);
        }
    }

    public SingleQuadParticle.@NonNull Layer getLayer() {
        return SingleQuadParticle.Layer.OPAQUE;
    }

    @Environment(EnvType.CLIENT)
    public static class Provider implements ParticleProvider<SimpleParticleType> {
        private final SpriteSet sprites;

        public Provider(final SpriteSet sprites) {
            this.sprites = sprites;
        }

        public Particle createParticle(final SimpleParticleType options, final ClientLevel level, final double x, final double y, final double z, final double xAux, final double yAux, final double zAux, final RandomSource random) {
            return new FullSweepParticle(level, x, y, z, xAux, this.sprites);
        }
    }
}
