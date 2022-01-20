import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';

import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { AdminComponent } from './admin/admin.component';
import { HomeComponent } from './home/home.component';
import {
  MatAutocompleteModule
} from '@angular/material/autocomplete';

import {
  MatCardModule
} from '@angular/material/card';

import {
  MatInputModule
} from '@angular/material/input';


import {
  MatTableModule
} from '@angular/material/table';

import {
MatButtonModule
} from '@angular/material/button';
import {MatFormFieldControl, MatFormFieldModule } from '@angular/material/form-field';
import { MatCommonModule } from '@angular/material/core';

import {MatSnackBarModule} from '@angular/material/snack-bar';
import { HttpClientModule } from '@angular/common/http';
import { NotificationComponent } from './notification/notification.component';
import { MatCheckboxModule } from '@angular/material/checkbox';
import { UserComponent } from './user/user.component';
import {MatBadgeModule} from '@angular/material/badge';
@NgModule({
  declarations: [
    AppComponent,
    AdminComponent,
    HomeComponent,
    NotificationComponent,
    UserComponent
  ],
  imports: [
    BrowserModule,
    AppRoutingModule,
    BrowserAnimationsModule,
    MatCommonModule,
    MatAutocompleteModule,
    MatCardModule,
    MatInputModule,
    MatButtonModule,
    MatFormFieldModule,
    MatTableModule,
    MatSnackBarModule,
    HttpClientModule,
    MatFormFieldModule,
    MatCheckboxModule,
    MatBadgeModule
    
  ],
  providers: [],
  bootstrap: [AppComponent],
  exports :[MatFormFieldModule, MatInputModule]
})
export class AppModule { }
