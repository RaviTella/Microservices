using System;
using System.Collections.Generic;
using System.Linq;
using System.Threading.Tasks;

namespace eBookStore.Models
{
    public class Membership
    {
        public string id { get; set; }
        public int points { get; set; }
        public string type { get; set; }
        public int customerID { get; set; }
    }
}
