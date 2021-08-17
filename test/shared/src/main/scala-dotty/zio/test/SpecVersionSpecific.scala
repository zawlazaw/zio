package zio.test

import zio.{ZIO, Has, ZLayer}
import zio.test.environment.TestEnvironment

trait SpecVersionSpecific[-R, +E, +T] { self: Spec[R, E, T] =>

  /**
   * Automatically assembles a layer for the spec, translating it up a level.
   */
  inline def inject[E1 >: E](inline layers: ZLayer[_, E1, _]*): Spec[Any, E1, T] =
    ${SpecLayerMacros.injectImpl[Any, R, E1, T]('self, 'layers)}

  def injectSome[R0 ] =
    new InjectSomePartiallyApplied[R0, R, E, T](self)

  def injectSomeShared[R0 ] =
    new InjectSomeSharedPartiallyApplied[R0, R, E, T](self)

  /**
   * Automatically constructs the part of the environment that is not part of the
   * `TestEnvironment`, leaving an effect that only depends on the `TestEnvironment`.
   * This will also satisfy transitive `TestEnvironment` requirements with
   * `TestEnvironment.any`, allowing them to be provided later.
   *
   * {{{
   * val zio: ZIO[OldLady with Console, Nothing, Unit] = ???
   * val oldLadyLayer: ZLayer[Fly, Nothing, OldLady] = ???
   * val flyLayer: ZLayer[Blocking, Nothing, Fly] = ???
   *
   * // The TestEnvironment you use later will provide both Blocking to flyLayer and
   * // Console to zio
   * val zio2 : ZIO[TestEnvironment, Nothing, Unit] =
   *   zio.injectCustom(oldLadyLayer, flyLayer)
   * }}}
   */
  inline def injectCustom[E1 >: E](inline layers: ZLayer[_, E1, _]*): Spec[TestEnvironment, E1, T] =
    ${SpecLayerMacros.injectImpl[TestEnvironment, R, E1, T]('self, 'layers)}

  /**
   * Automatically assembles a layer for the spec, sharing services between all tests.
   */
  inline def injectShared[E1 >: E](inline layers: ZLayer[_, E1, _]*): Spec[Any, E1, T] =
    ${SpecLayerMacros.injectSharedImpl[Any, R, E1, T]('self, 'layers)}

  /**
   * Automatically constructs the part of the environment that is not part of the
   * `TestEnvironment`, leaving an effect that only depends on the `TestEnvironment`,
   * sharing services between all tests.
   *
   * This will also satisfy transitive `TestEnvironment` requirements with
   * `TestEnvironment.any`, allowing them to be provided later.
   *
   * {{{
   * val zio: ZIO[OldLady with Console, Nothing, Unit] = ???
   * val oldLadyLayer: ZLayer[Fly, Nothing, OldLady] = ???
   * val flyLayer: ZLayer[Blocking, Nothing, Fly] = ???
   *
   * // The TestEnvironment you use later will provide both Blocking to flyLayer and
   * // Console to zio
   * val zio2 : ZIO[TestEnvironment, Nothing, Unit] =
   *   zio.injectCustom(oldLadyLayer, flyLayer)
   * }}}
   */
  inline def injectCustomShared[E1 >: E](inline layers: ZLayer[_, E1, _]*): Spec[TestEnvironment, E1, T] =
    ${SpecLayerMacros.injectSharedImpl[TestEnvironment, R, E1, T]('self, 'layers)}
}

private final class InjectSomePartiallyApplied[R0, -R, +E, +T](val self: Spec[R, E, T]) extends AnyVal {
  inline def apply[E1 >: E](inline layers: ZLayer[_, E1, _]*): Spec[R0, E1, T] =
  ${SpecLayerMacros.injectImpl[R0, R, E1, T]('self, 'layers)}
}

private final class InjectSomeSharedPartiallyApplied[R0, -R, +E, +T](val self: Spec[R, E, T]) extends AnyVal {
  inline def apply[E1 >: E](inline layers: ZLayer[_, E1, _]*): Spec[R0, E1, T] =
  ${SpecLayerMacros.injectSharedImpl[R0, R, E1, T]('self, 'layers)}
}
