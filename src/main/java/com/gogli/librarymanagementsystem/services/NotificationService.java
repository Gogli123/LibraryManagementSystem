package com.gogli.librarymanagementsystem.services;

import com.gogli.librarymanagementsystem.models.Patron;
import com.gogli.librarymanagementsystem.models.Transactions;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final JavaMailSender mailSender;

    public void sendOverdueNotifications(List<Transactions> overdueTransactions) {
        Map<Patron, List<Transactions>> patronTransactionsMap = overdueTransactions.stream()
                .collect(Collectors.groupingBy(Transactions::getPatron));

        // Send one notification per patron with all their overdue books
        patronTransactionsMap.forEach((patron, transactions) -> {
            StringBuilder bookDetails = new StringBuilder();
            for (Transactions transaction : transactions) {
                bookDetails.append("- ").append(transaction.getBook().getTitle())
                        .append(" (borrowed on: ").append(transaction.getBorrowedAt().format(DateTimeFormatter.ofPattern("dd-MM-yyyy")))
                        .append(", due date: ").append(transaction.getDueDate().format(DateTimeFormatter.ofPattern("dd-MM-yyyy")))
                        .append(")\n");
            }

            String subject = "Library Books Overdue Notice";
            String text = "Hi " + patron.getFirstName() + ",\n\n" +
                    "These books are overdue:\n\n" +
                    bookDetails +
                    "\nPlease return them as soon as possible.\n\n" +
                    "Thanks & Regards,\nLibrary Administration";

            sendMail(patron.getEmailAddress(), subject, text);
        });
    }
    
    private void sendMail(String to, String subject, String text) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(to);
            message.setSubject(subject);
            message.setText(text);
            mailSender.send(message);
        } catch (MailException e) {
            throw new RuntimeException("Failed to send email to " + to);
        }
    }
}
