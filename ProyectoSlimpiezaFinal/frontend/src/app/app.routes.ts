import { Routes } from '@angular/router';
import { Home } from './pages/home/home';
import { Booking } from './pages/booking/booking';
import { Requests } from './pages/requests/requests';

export const routes: Routes = [
  { path: '', component: Home },
  { path: 'booking', component: Booking },
  { path: 'requests', component: Requests },
  { path: '**', redirectTo: '' }
];
