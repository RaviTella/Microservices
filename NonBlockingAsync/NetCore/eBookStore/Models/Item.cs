using System;
using System.Collections.Generic;
using System.Linq;
using System.Threading.Tasks;

namespace eBookStore.Models
{
    public class Item
    {
        public string id { get; set; }
        public string name { get; set; }
        public string imageUrl { get; set; }
        public double price {get; set;}
        public int customerID { get; set; }

    }
}
