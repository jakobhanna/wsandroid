package fi.bitrite.android.ws.api.response;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import fi.bitrite.android.ws.model.MessageThread;
import fi.bitrite.android.ws.util.Pushable;

public class MessageThreadResponse {
    public class Participant {
        @SerializedName("uid") public int userId;
        public String name;
        public String fullname;
    }

    public class Message {
        @SerializedName("mid") public int id;
        @SerializedName("author") public int authorId;
        @SerializedName("timestamp") public Date date;
        public String body;
        public boolean isNew;

        public fi.bitrite.android.ws.model.Message toMessage(int threadId) {
            return new fi.bitrite.android.ws.model.Message(
                    id, threadId, authorId, date, body, isNew, true);
        }
    }

    @SerializedName("pmtid") public int id;
    public String subject;
    public List<Participant> participants;
    public List<Message> messages;

    public MessageThread toMessageThread(Pushable<Boolean> readStatus, Date started,
                                         Date lastUpdated) {
        // Prepares the participants.
        List<Integer> participantIds = new ArrayList<>(participants.size());
        for (Participant participant : participants) {
            participantIds.add(participant.userId);
        }

        // Prepares the messages.
        List<fi.bitrite.android.ws.model.Message> msgs = new ArrayList<>(messages.size());
        for (Message apiMessage : messages) {
            msgs.add(apiMessage.toMessage(id));
        }

        return new MessageThread(
                id, subject, started, readStatus, participantIds, msgs, lastUpdated);
    }
}
