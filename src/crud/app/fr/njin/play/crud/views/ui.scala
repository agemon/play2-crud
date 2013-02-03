package fr.njin.play.crud.views.html

import play.api.i18n.Messages

object ui {

  def appName() = {
    Messages("crud.appname")
  }

  def formError(name: String) = {
    Messages("crud.form.error", name)
  }

  def listViewTitle(name: String) = {
    Messages("crud.list.page.title", name)
  }

  def listViewPageHeader(name: String) = {
    Messages("crud.list.page.header", name)
  }

  def listCreate(name: String) = {
    Messages("crud.list.create", name)
  }

  def listSearch(name: String) = {
    Messages("crud.list.search", name)
  }

  def listSearchCancel(name: String) = {
    Messages("crud.list.search.cancel", name)
  }

  def listChoose(name: String) = {
    Messages("crud.list.choose", name)
  }

  def listEdit(name: String) = {
    Messages("crud.list.edit", name)
  }
  def listView(name: String) = {
    Messages("crud.list.view", name)
  }

  def listDisplayingXofY(name: String, x: Int, y: Int) = {
    Messages("crud.list.displaying.x.of.y", name, x, y)
  }

  def paginatePrevious() = {
    Messages("pagination.previous");
  }
  def paginateNext() = {
    Messages("pagination.next");
  }

  def showViewTitle(name: String, id: String) = {
    Messages("crud.show.page.title", name, id)
  }

  def showViewPageHeader(name: String, id: String) = {
    Messages("crud.show.page.header", name, id)
  }

  def showReturnToList(name: String) = {
    Messages("crud.show.return.to.list", name)
  }

  def showEdit(name: String) = {
    Messages("crud.show.edit", name)
  }

  def showDelete(name: String) = {
    Messages("crud.show.delete", name)
  }

  def showDeleteConfirm(name: String, id: String) = {
    Messages("crud.show.confirm.delete", name, id)
  }

  def showModalLoading(name: String) = {
    Messages("crud.show.modal.loading", name)
  }

  def showModalClose(name: String) = {
    Messages("crud.show.modal.close", name)
  }

  def createViewTitle(name: String) = {
    Messages("crud.create.page.title", name)
  }

  def createViewPageHeader(name: String) = {
    Messages("crud.create.page.header", name)
  }

  def createCancel(name: String) = {
    Messages("crud.create.cancel", name)
  }

  def createSave(name: String) = {
    Messages("crud.create.save", name)
  }

  def editViewTitle(name: String, id: String) = {
    Messages("crud.edit.page.title", name, id)
  }

  def editViewPageHeader(name: String, id: String) = {
    Messages("crud.edit.page.header", name, id)
  }

  def editCancel(name: String, id: String) = {
    Messages("crud.edit.cancel", name, id)
  }

  def editSave(name: String, id: String) = {
    Messages("crud.edit.save", name, id)
  }

  def formSelectNone(name: String) = {
    Messages("crud.form.select.none", name)
  }

  def formRelationChoose(name: String) = {
    Messages("crud.form.relation.add", name)
  }

  def formRelationMultipleChoose(name: String) = {
    Messages("crud.form.relation.multiple.add", name)
  }

  def flashCreateSuccess(name: String, id: String) = {
    Messages("crud.flash.create.success", name, id);
  }

  def flashCreateFailed(name: String) = {
    Messages("crud.flash.create.failed", name);
  }

  def flashUpdateSuccess(name: String, id: String) = {
    Messages("crud.flash.update.success", name, id);
  }

  def flashUpdateFailed(name: String, id: String) = {
    Messages("crud.flash.update.failed", name, id);
  }
}