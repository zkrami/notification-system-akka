import { Component, OnInit } from '@angular/core';
import { firstValueFrom, Observable, ReplaySubject } from 'rxjs';
import { DefaultService, Identifier, InlineResponse200, Notification } from 'src/service';
import { MatSnackBar } from '@angular/material/snack-bar';
import { DataSource, SelectionModel } from '@angular/cdk/collections';
import { Router } from '@angular/router';


@Component({
  selector: 'app-admin',
  templateUrl: './admin.component.html',
  styleUrls: ['./admin.component.scss']
})
export class AdminComponent implements OnInit {

  constructor(private service: DefaultService, private _snackBar: MatSnackBar, private router: Router) { }

  displayedColumnsIdentifier = ["identifier", "delete"]
  dataSourceIdentifier: Identifier[] = [];

  displayedIdentifierSelectionColumns = ["identifier", "select"]

  selection = new SelectionModel<Identifier>(true, []);
  systemStats: InlineResponse200 = {
    sent: 0,
    delivered: 0
  }

  isAllSelected() {
    const numSelected = this.selection.selected.length;
    const numRows = this.dataSourceIdentifier.length;
    return numSelected === numRows;
  }
  masterToggle() {
    if (this.isAllSelected()) {
      this.selection.clear();
      return;
    }

    this.selection.select(...this.dataSourceIdentifier);
  }


  async loadStats() {

    this.systemStats = await firstValueFrom(this.service.statsGet());

  }
  async loadIdentifiers() {

    this.dataSourceIdentifier = await firstValueFrom(this.service.identifiersGet());

  }


  displayedColumnsNotification = ["id", "message", "date", "detail"]
  dataSourceNotification: Notification[] = [];

  async loadNotifications() {

    this.dataSourceNotification = await firstValueFrom(this.service.notificationsGet());
    
  }



  ngOnInit(): void {

    this.loadIdentifiers();
    this.loadNotifications();
    this.loadStats(); 

  }


  log(message: any) {
    console.log("@LOG ", message);
  }

  async deleteIdentifier(identifierId: string) {


    try {
      await firstValueFrom(this.service.identifiersIdentifierDelete(identifierId));
      this._snackBar.open("Identifier Deleted Successfully ✅", "OK");

    } catch (ex: any) {
      if (ex.status !== 200) {
        this._snackBar.open("Identifier Could not Be Deleted ❌", "OK");
        this.log(ex);
      } else {
        this._snackBar.open("Identifier Deleted Successfully ✅", "OK");

      }

    } finally {
      this.loadIdentifiers();
    }

  }

  async postNotification(message: string) {

    try {
      await firstValueFrom(this.service.notificationsPost({ message, identifiers: this.selection.selected }));
      this._snackBar.open("Notification Posted Successfully ✅", "OK");
    } catch (ex: any) {
      if (ex.status !== 200) {
        this._snackBar.open("Notification Could not Be Posted ❌", "OK");
        this.log(ex);
      } else {
        this._snackBar.open("Notification Posted Successfully ✅", "OK");

      }
    } finally {
      this.loadNotifications();
      this.loadStats(); 
    }

  }

  async addIdentifier(identifier: string) {


    try {
      await firstValueFrom(this.service.identifiersPost({ identifier: identifier }));
      this._snackBar.open("Identifier Added Successfully ✅", "OK");
    } catch (ex: any) {
      if (ex.status !== 200) {
        this._snackBar.open("Identifier Could not Be Added ❌", "OK");
        this.log(ex);

      } else {
        this._snackBar.open("Identifier Added Successfully ✅", "OK");

      }

    } finally {
      this.loadIdentifiers();
    }
  }
}

