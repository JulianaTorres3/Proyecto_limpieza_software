import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class BookingService {
  private apiUrl = 'http://localhost:8080/api/requests';

  constructor(private http: HttpClient) { }

  createBooking(data: any): Observable<any> {
    return this.http.post(this.apiUrl, data);
  }

  getRequests(): Observable<any[]> {
    return this.http.get<any[]>(this.apiUrl);
  }

  updateRequest(id: number, data: any): Observable<any> {
    return this.http.put(`${this.apiUrl}/${id}`, data);
  }

  deleteRequest(id: number): Observable<any> {
    return this.http.delete(`${this.apiUrl}/${id}`);
  }

  getStatsByMonth(): Observable<any[]> {
    return this.http.get<any[]>(`${this.apiUrl}/stats/by-month`);
  }

  getStatsByType(): Observable<any[]> {
    return this.http.get<any[]>(`${this.apiUrl}/stats/by-type`);
  }
}
