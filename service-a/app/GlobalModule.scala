import com.google.inject.AbstractModule


class GlobalModule extends AbstractModule {
  override def configure(): Unit = {
    bind(classOf[GlobalStart]).asEagerSingleton
  }
}
