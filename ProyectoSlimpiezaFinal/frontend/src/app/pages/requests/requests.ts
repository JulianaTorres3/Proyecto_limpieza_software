import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { BookingService } from '../../services/booking.service';

@Component({
  selector: 'app-requests',
  imports: [CommonModule, FormsModule],
  templateUrl: './requests.html'
})
export class Requests implements OnInit {
  requests: any[] = [];
  editingId: number | null = null;
  editData: any = {};
  isSaving = false;
  isSaveSuccess = false;
  
  constructor(private bookingService: BookingService) {}

  ngOnInit() {
    this.loadRequests();
  }

  loadRequests() {
    this.bookingService.getRequests().subscribe({
      next: (data) => this.requests = data,
      error: (err) => console.error('Error fetching requests', err)
    });
  }

  deleteRequest(id: number) {
    if(confirm('¿Estás seguro que deseas eliminar esta reserva de forma permanente?')) {
      this.bookingService.deleteRequest(id).subscribe(() => {
        this.loadRequests();
      });
    }
  }

  startEdit(req: any) {
    this.editingId = req.id;
    this.editData = { ...req };
  }

  cancelEdit() {
    this.editingId = null;
    this.isSaving = false;
    this.isSaveSuccess = false;
  }

  saveEdit() {
    this.isSaving = true;
    this.isSaveSuccess = false;
    this.bookingService.updateRequest(this.editingId!, this.editData).subscribe(() => {
      this.isSaving = false;
      this.isSaveSuccess = true;
      this.loadRequests();
      setTimeout(() => {
        this.editingId = null;
        this.isSaveSuccess = false;
      }, 1500);
    });
  }
}
