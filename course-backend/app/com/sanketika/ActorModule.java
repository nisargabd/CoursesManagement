package com.sanketika;

import com.google.inject.AbstractModule;
import com.sanketika.actors.CourseActor;
import play.libs.akka.AkkaGuiceSupport;

public class ActorModule extends AbstractModule implements AkkaGuiceSupport {
    @Override
    protected void configure() {
        bindActor(CourseActor.class, "courseActor");
    }
}
