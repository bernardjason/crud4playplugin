package controllers

import com.google.inject.AbstractModule

class Module extends AbstractModule {
  def configure() = {
    bind(classOf[org.bjason.blog.blog.controllers.CrudActionTrait])
      .to(classOf[SecuredCrudBlog])
    bind(classOf[org.bjason.blog.user.controllers.CrudActionTrait])
      .to(classOf[SecuredCrudUser])
  }
}
