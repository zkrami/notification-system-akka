import { Component, OnInit } from '@angular/core';
import { MatSnackBar } from '@angular/material/snack-bar';
import { firstValueFrom } from 'rxjs';
import { DefaultService } from 'src/service';

@Component({
  selector: 'app-user',
  templateUrl: './user.component.html',
  styleUrls: ['./user.component.scss']
})
export class UserComponent implements OnInit {


  constructor(private service: DefaultService, private _snackBar: MatSnackBar) { }

  notificationId?: string;

  displayedColumns = ["identifier", "read"]

  dataSource: any = []

  displayedColumnsNotification = ["id", "message", "date", "read"]


  ngOnInit(): void {

  }
  
  async fetch(identifier: string, key: string) {

    try {
      let response = await firstValueFrom(this.service.notificationsIdentifierGet(identifier, key));
      this.dataSource = response;
      this._snackBar.open("Identifier Notification Loaded Successfully ✅", "OK");
    } catch (ex) {
      this._snackBar.open("Identifier Notification Could not Be Loaded ❌", "OK");
    }

  }

}
