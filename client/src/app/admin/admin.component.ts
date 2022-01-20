import { Component, OnInit } from '@angular/core';
import { firstValueFrom, Observable, ReplaySubject } from 'rxjs';
import { DefaultService, Identifier } from 'src/service';
import { MatSnackBar } from '@angular/material/snack-bar';
import { DataSource } from '@angular/cdk/collections';


@Component({
  selector: 'app-admin',
  templateUrl: './admin.component.html',
  styleUrls: ['./admin.component.scss']
})
export class AdminComponent implements OnInit {

  constructor(private service: DefaultService, private _snackBar: MatSnackBar) { }

  displayedColumns = ["identifier" , "delete"] 
  dataSource: Identifier[] = [];

  async loadIdentifiers(){

    this.dataSource =  await firstValueFrom(this.service.identifiersGet());

  }
  ngOnInit(): void {

    this.loadIdentifiers(); 
  }


  log(message: any) {
    console.log("@LOG ", message);
  }

  async addIdentifier(identifier: string) {


    try {
      await firstValueFrom(this.service.identifiersPost({ identifier: identifier }));
      this._snackBar.open("Identifier Added Successfully ✅", "OK");
    } catch (ex) {
      this._snackBar.open("Identifier Could not Be Added ❌", "OK");
      this.log(ex);
    }
  }
}

