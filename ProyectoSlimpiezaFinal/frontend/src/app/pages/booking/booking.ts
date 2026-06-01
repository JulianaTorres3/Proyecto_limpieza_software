import { Component } from '@angular/core';
import { RouterLink } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { BookingService } from '../../services/booking.service';

@Component({
  selector: 'app-booking',
  imports: [RouterLink, FormsModule, CommonModule],
  templateUrl: './booking.html',
  styleUrl: './booking.css',
})
export class Booking {
  bookingData = {
    clientName: '',
    phone: '',
    email: '',
    address: '',
    serviceType: 'HOGAR',
    serviceDate: '',
    additionalNotes: ''
  };

  isSubmitting = false;
  isSuccess = false;
  successMessage = '';
  errorMessage = '';

  constructor(private bookingService: BookingService) {}

  onSubmit() {
    this.isSubmitting = true;
    this.isSuccess = false;
    this.successMessage = '';
    this.errorMessage = '';

    const combinedNotes = `Email: ${this.bookingData.email} | Teléfono: ${this.bookingData.phone} | Notas: ${this.bookingData.additionalNotes}`;

    const payload = {
      clientName: this.bookingData.clientName,
      address: this.bookingData.address,
      serviceType: this.bookingData.serviceType,
      serviceDate: this.bookingData.serviceDate,
      additionalNotes: combinedNotes
    };

    this.bookingService.createBooking(payload).subscribe({
      next: () => {
        this.isSubmitting = false;
        this.isSuccess = true;
        this.successMessage = '¡Servicio reservado con éxito! Pronto nos contactaremos contigo.';
        this.bookingData = { clientName: '', phone: '', email: '', address: '', serviceType: 'HOGAR', serviceDate: '', additionalNotes: '' };

        setTimeout(() => {
          this.isSuccess = false;
          this.successMessage = '';
        }, 5000);
      },
      error: (err) => {
        this.isSubmitting = false;
        this.errorMessage = 'Hubo un error al enviar la reserva. Asegúrate de que el backend (localhost:8080) esté corriendo.';
        console.error('Error en la reserva:', err);
      }
    });
  }
}
